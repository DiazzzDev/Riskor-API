package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Getter @Setter
public class DTOEmployee {
    private static final String DUI_REGEX = "^[0-9]{8}-[0-9]$";

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Aunque el cliente quiera forzar un ID Será ignorado
    private String idEmployee;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 75, message = "El nombre debe contener entre 3 a 75 carácteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ'’ ]{3,75}$",
            message = "El nombre contiene carácteres no válidos"
    )
    private String firstName;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 3, max = 75, message = "El apellido debe contener entre 3 a 75 carácteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ'’ ]{3,75}$",
            message = "El nombre contiene carácteres no válidos"
    )
    private String lastName;

    @NotBlank @Size(max = 1)
    @Pattern(regexp = "^[MF]{1}$") //Al usar esto solo se pondrá permitir M y F como opc de género
    private String gender;

    private Integer age;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @NotBlank(message = "La persona debe contener un Documento Único de Identidad")
    @Size(max = 10, message = "Documento Único de Identidad tiene un máximo de 10 carácteres")
    @Pattern(regexp = DUI_REGEX, message = "El formato del Documento Único de Identidad no es válido. Debe ser XXXXXXXX-Y.")
    private String dui;

    @NotNull(message = "La persona debe contener un número de afiliación ISSS")
    private Double affiliationISSS;

    @Nullable
    @Size(max = 500, message = "La dirección tiene un máximo de 500 carácteres")
    private String address;

    @Nullable
    @Size(max = 9)
    @Pattern(regexp = "^[267]\\d{3}[- ]?\\d{4}$", message = "Número de teléfono inválido (9 dígitos, comienza con 2, 6 o 7)")
    private String personalPhone;

    //@NotBlank(message = "La persona debe contener una fotografía")
    //@Size(min = 5, max = 1000, message = "El enlace de la fotografía debe tener de 5 a 1000 carácteres")
    private String photo;

    @Email @NotBlank
    @Size(min = 5, max = 125, message = "El correo electrónico del empleado debe tener de 5 a 125 carácteres")
    private String employeeMail;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank
    private String idRole;

    @NotBlank
    private String username;

    private LocalDate userDetails;

    @Nullable //Ayuda a detectar posibles NullPointerExceptions, teniendo mayor control de excepciones
    private String idCommitteePosition;

    @Nullable
    private String idCommitteeRole;

    @NotBlank
    private String idEmployeePosition;

    private String employeePosition;

    //Se agrega para evitar que un atacante modifique el JSON cambiando el ID y modificando registros de otras empresas
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String idBusiness;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String committePosition;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String committeRole;
}
