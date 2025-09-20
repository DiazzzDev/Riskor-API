package RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DTOTrainingRatings {
    //Este DTO es para crear el formato del JSON personalizado para mostrar las capacitaciones y las calificaciones que han mandado los empleados
    private String idTraining;
    private String trainingName; //Nombre de la capacitación

    // agregados para UI
    private long total;      //Total de evaluaciones
    private double average;  //Promedio de calificaciones de cada capacitación (ej. 2.5)

    //Distribución para las barras
    private long stars5;
    private long stars4;
    private long stars3;
    private long stars2;
    private long stars1;
}
