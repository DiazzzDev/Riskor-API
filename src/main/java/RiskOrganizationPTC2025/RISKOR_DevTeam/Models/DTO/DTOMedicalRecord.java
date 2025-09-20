package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Getter @Setter
public class DTOMedicalRecord {
    //Tomamos el ID del empleado del path, no del body (JSON)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idMedicalRecord;

    @Nullable @Size(max = 250, message = "El máximo de carácteres en las alergías son 250")
    private String allergie;

    @NotBlank @Size(max = 125, message = "El contacto de emergencia debe contener entre 3 a 125 carácteres")
    private String contactName;

    @NotBlank @Size(max = 9, message = "El número de contacto debe contener un máximo de 9 carácteres")
    private String contactPhone;

    @Nullable @Size(max = 250, message = "Las necesidades especiales tienen un máximo de 250 carácteres")
    private String specialNeed;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Vamos a facilitar la vida a los front y no vamos a pedir los campos, los vamos a settear en backend
    private LocalDate creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate lastUpdate;

    @NotBlank(message = "Se debe agregar el tipo de sangre del empleado")
    private String idBloodType;

    @NotBlank(message = "Se debe asignar un empleado")
    private String idEmployee;

    //Este campo no debe ser aceptado por el cliente, el service y el controller lo setean (Si lo hiciera el cliente gran falla de seguridad)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    private String idBusiness;
}