package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.EntityUser;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOUser;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.RepositoryUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceUser {
    @Autowired
    private RepositoryUser objRepoU;

    //Inyección de interfaz PasswordEncoder para usar argon2 en conversión de contraseña segura
    @Autowired
    private PasswordEncoder argon2id;

    public DTOUser postUser(@Valid DTOUser dtoU){
        if (dtoU == null){ throw new IllegalArgumentException("No pueden haber campos vacíos"); }
        try{
            //Se aplica hash la contraseña y se guarda en un string
            String rawPwd = dtoU.getPassword();
            String hashed = argon2id.encode(rawPwd);

            //Se sobreescribe la contraseña del objDTO que se va a guardar
            dtoU.setPassword(hashed);

            //Guardamos todo y la contraseña ahora debe ser segura
            EntityUser saved = objRepoU.save(convertToEU(dtoU));
            return convertToDTOU(saved);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public DTOUser putUser(@Valid DTOUser dtoU, String username){
        //Validamos que se haya enviado un JSON válido
        if (dtoU == null) { throw new IllegalArgumentException("No pueden haber campos vacíos"); }
        //Verificamos que el usuario exista, caso contrario enviamos un 404 no encontrado
        EntityUser user = objRepoU.findById(username).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        user.setUsername(dtoU.getUsername());

        //Se vuelve a encriptar la contraseña
        String rawPwd = dtoU.getPassword(); //Se obtiene la contraseña que ya posee
        //String hashed = argon2id.encode(rawPwd); //Esa contraseña se encripta
        user.setPassword(rawPwd); //Se guarda la contraseña ahora encriptada

        user.setStatus(dtoU.getStatus());
        return convertToDTOU(user); //Omitimos SAVE porque JPA sincroniza por @TRANSACTIONAL
    }

    public DTOUser patchUserStatus(String username, String value) {
        //Validamos que se ha recibido un nombre de usuario
        if (username == null || username.trim().isEmpty()) { throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");}

        //Se prepara el valor recibido a como se va a enviar a la db con uso de un operador ternario (Se pasa a mayúscula)
        String newStatus = (value == null ? "" : value.trim().toUpperCase());

        //Confirmamos la existencia del usuario, sino lanzamos 404
        EntityUser user = objRepoU.findById(username).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        //Validamos que el usuario no tenga el mismo estado al que se le quiere cambiar
        if (newStatus.equals(user.getStatus())) {
            return convertToDTOU(user); //Si es así devolvemos el usuario tal como está, sin cambiar nada
        }

        //Asignamos al usuario encontrado el nuevo estatus en lugar de actualizar todo el usuario - Por eso el uso de patch
        user.setStatus(newStatus);
        return convertToDTOU(user); //Convertimos a DTO para enviarlo al controller, evitamos SAVE porque usamos @transactional
    }

    private DTOUser convertToDTOU(EntityUser eU){
        DTOUser dtoU = new DTOUser();
        dtoU.setUsername(eU.getUsername());
        //dtoU.setPassword(eU.getPassword());
        dtoU.setStatus(eU.getStatus());
        dtoU.setCreationDate(eU.getCreationDate());

        return dtoU;
    }

    private EntityUser convertToEU(DTOUser dtoU){
        EntityUser user = new EntityUser();
        user.setUsername(dtoU.getUsername());
        user.setPassword(dtoU.getPassword());
        //DATOS POR DEFECTO
        user.setStatus("T");
        user.setCreationDate(LocalDate.now());

        return user;
    }
}
