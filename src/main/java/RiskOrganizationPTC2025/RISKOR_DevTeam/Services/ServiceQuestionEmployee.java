package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityQuestionEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions.ExceptionDataNotFound;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOQuestionEmployee;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryQuestionEmployee;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceQuestionEmployee {
    //Inyectamos el repositorio
    @Autowired
    private RepositoryQuestionEmployee objRepoQuestionE;

    //Método para retornar una lista de todos los registros dentro de la tabla referenciada
    @Transactional(readOnly = true)
    public List<DTOQuestionEmployee> getAllQuestionE(){
        List<EntityQuestionEmployee> objGetQuestionE = objRepoQuestionE.findAll();
        return objGetQuestionE.stream().map(this::convertTOQuestionEDTO).collect(Collectors.toList());
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    public DTOQuestionEmployee postQuestionE(DTOQuestionEmployee DTOQuestionEmployee){
        //Si los datos recibidos en el DTO (dependiendo de la base de datos, las restricciones) ES NULL, se mandará un mensaje de error indicando campos vacíos
        if (DTOQuestionEmployee == null){
            throw new IllegalArgumentException("No pueden haber campos vacíos");
        }
        //Caso contrario, se procede con la inserción de datos (POST)
        EntityQuestionEmployee objeQuestionESaved = objRepoQuestionE.save(convertTOQuestionEEntity(DTOQuestionEmployee));
        //Finalmente, retornamos los valores que reciben como parámetro la entidad, relacionandose con la DB
        return convertTOQuestionEDTO(objeQuestionESaved);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos para el PUT el DTO de la clase (DB) y el ID para especificar el registro
    public DTOQuestionEmployee putQuestionEmployee(DTOQuestionEmployee dtoQuestionEmployee, String idQuestionEmployee) {
        //Validamos que el DTO no venga vacío
        if (dtoQuestionEmployee == null) {
            throw new IllegalArgumentException("No pueden haber campos vacíos");
        }

        //Buscamos si existe el registro con el ID proporcionado
        EntityQuestionEmployee objQuestionExists = objRepoQuestionE.findById(idQuestionEmployee).orElseThrow(() -> new EntityNotFoundException("Pregunta de seguridad no encontrada con ID: " + idQuestionEmployee));

        //Actualizamos los campos
        objQuestionExists.setAnswerEmployee(dtoQuestionEmployee.getAnswerEmployee());

        //Guardamos la entidad actualizada
        EntityQuestionEmployee objEntityUpdated = objRepoQuestionE.save(objQuestionExists);

        //Retornamos el DTO actualizado
        return convertTOQuestionEDTO(objEntityUpdated);
    }

    //Este método retornará los valores de las claves ingresadas para poder ser registradas dentro de la DB
    //Indicamos que en el DELETE se especificará UNICAMENTE el ID
    public boolean deleteQuestionE(String idQuestionE){
        try{
            //Si el ID del Equipo Prestado no es especificado o ingresado, retornamos una excepción
            if (idQuestionE == null || idQuestionE.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID de la Pregunta de Seguridad no puede ser nulo o vacío");
            }

            //Si existe el ID, guardamos el valor
            boolean exists = objRepoQuestionE.existsById(idQuestionE);
            //Condicional para la existencia
            if (!exists) {
                throw new EntityNotFoundException("No se encontró la Pregunta de Seguridad con ID: " + idQuestionE);
            }
            //Retornamos el valor con el ID y se elimina el registro especificado
            objRepoQuestionE.deleteById(idQuestionE);
            return true;
        }
        catch (ExceptionDataNotFound e){
            throw new EntityNotFoundException("No se encontró el Equipo EPP Prestado");
        }
    }

    //Método para conversión de datos del DTO hacia la Entidad (método de arriba)
    private DTOQuestionEmployee convertTOQuestionEDTO(EntityQuestionEmployee questionEmployee){
        DTOQuestionEmployee objQuestionEDTO = new DTOQuestionEmployee();
        objQuestionEDTO.setIdQuestionsEmployee(questionEmployee.getIdQuestionsEmployee());
        objQuestionEDTO.setAnswerEmployee(questionEmployee.getAnswerEmployee());
        objQuestionEDTO.setIdEmployee(questionEmployee.getIdEmployee());
        objQuestionEDTO.setIdQuestion(questionEmployee.getIdQuestion());
        return objQuestionEDTO;
    }

    //Método para conversión de datos de la ENTIDAD hacia el DTO (método de arriba)
    private EntityQuestionEmployee convertTOQuestionEEntity(DTOQuestionEmployee DTOQuestionEmployee){
        EntityQuestionEmployee objEntityQuestionE = new EntityQuestionEmployee();
        objEntityQuestionE.setAnswerEmployee(DTOQuestionEmployee.getAnswerEmployee());
        objEntityQuestionE.setIdEmployee(DTOQuestionEmployee.getIdEmployee());
        objEntityQuestionE.setIdQuestion(DTOQuestionEmployee.getIdQuestion());
        return objEntityQuestionE;
    }
}
