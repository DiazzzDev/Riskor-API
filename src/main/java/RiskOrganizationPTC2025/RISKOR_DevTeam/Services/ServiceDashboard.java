package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.Dashboard.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Entities.*;
import RiskOrganizationPTC2025.RISKOR_DevTeam.Repositories.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) //Indicamos que todos los métodos de esta clase serán solo GETs o de lectura, mejorando el rendimiento de la API
public class ServiceDashboard {
    //region Inyección de repositorios que se usan para dashboard
    @Autowired
    private RepositoryTrainingEmployee teRepo;

    @Autowired
    private RepositoryTraining trRepo;

    @Autowired
    private RepositoryEmployee empRepo;

    @Autowired
    private RepositoryEmployeePosition posRepo;

    @Autowired
    private RepositoryAccident accRepo;

    @Autowired
    private RepositoryLocation locRepo;

    @Autowired
    private RepositoryTrainingRating ratingRepo;
    //endregion

    //Método que cuenta la cantidad de capacitaciones en las que el empleado ha participado
    public DTOAttendanceSummary attendance(String idBusiness, String emp){
        //Primero obtenemos la consulta donde buscamos por negocio el empleado dentro de la capacitación
        List<EntityTrainingEmployee> rows = teRepo.findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(idBusiness.toUpperCase(), emp.toUpperCase());

        //Dividimos la consulta en 2, y filtra si asistieron ("S") y cuenta los registros, igualmente con las faltas
        long attended = rows.stream().filter(r -> "S".equalsIgnoreCase(r.getAttendance())).count();
        long missed = rows.stream().filter(r -> "N".equalsIgnoreCase(r.getAttendance())).count();

        return new DTOAttendanceSummary(attended, missed); //Retorna un JSON personalizado donde vamos a mandar las asistencias y faltas
    }

    //Calendario (fechas y su título (En el gráfico primero se va a mostrar solo la fecha porque el título es por temas de escalabilidad))
    public List<DTOCalendarItem> calendar(String biz, String emp){
        var rows = teRepo.findByIdBusiness_IdBusinessAndIdEmployee_IdEmployee(biz.toUpperCase(), emp.toUpperCase()); //Vamos a buscar el empleado
        if (rows.isEmpty()) return List.of(); //Si no encuentra capacitaciones devuelve una lista VACÍA

        //Vamos a guardar todos los ID de las capacitaciones encontradas con lo siguiente:
        //distinct para evitar duplicados, map para transformar cada fila en su correspondiente capacitación y toList con stream para guardar todo en una Lista de JAVA
        List<String> trainingIds = rows.stream().map(r -> r.getIdTraining().getIdTraining()).distinct().toList();

        //Se van a devolver todas las capacitaciones donde las devolvemos en un formato listo para usarse en calendario desde JS
        return trRepo.findByIdBusiness_IdBusinessAndIdTrainingIn(biz.toUpperCase(), trainingIds)
                .stream() //Para procesar colecciones de datos de manera eficiente
                .sorted(Comparator.comparing(EntityTraining::getTrainingDate))//Ordenamos la lista de manera cronológica
                .map(t -> new DTOCalendarItem(t.getTrainingDate(), t.getTitle())) //Por cada capacitación encontrada solo va a devolver su fecha y su título
                .toList(); //Guardamos todo en una lista lista para mandar al frontend
    }

    //Accidentes: este mes y el mes anterior
    public DTOAccidentCompare accidentsCompare(String biz){
        //Obtenemos el mes actual
        LocalDate firstThis = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        //Obtenemos el mes siguiente (Para ampliar de todo el mes)
        LocalDate firstNext = firstThis.plusMonths(1);
        //Obtenemos el mes anterior para compararlo con el mes actual los accidentes sucedidos
        LocalDate firstPrev = firstThis.minusMonths(1);

        //This count, previous count se refieren a las variables que guardan la respuesta de los accidentes sucedidos este mes que se solicitó a la JPA
        long thisCnt = accRepo.countByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(biz.toUpperCase(), firstThis, firstNext);
        long prevCnt = accRepo.countByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(biz.toUpperCase(), firstPrev, firstThis);

        return new DTOAccidentCompare(thisCnt, prevCnt); //Devolvemos
    }

    //Serie por mes (min 2, max 6) - Gráfico accidentes sucedidos
    public List<DTOAccidentMonthCount> accidentsSeries(String biz, YearMonth startYm, int months){
        //Entre los meses que se pueda seleccionar se va a permitir un máximo de 6 meses y un mínimo de 2 meses para mostrar en el gráfico
        months = Math.max(2, Math.min(6, months));
        //Configuramos la fecha de inicio y fin que se va a solicitar a la JPA
        LocalDate start = startYm.atDay(1);
        LocalDate end   = start.plusMonths(months);

        //Vamos a solicitar todos los accidentes que:
        //Pertenezcan a una misma empresa
        //El accidente debe ser igual o posterior a la fecha de inicio
        //El accidente debe ser anterior a la fecha de fin
        var accs = accRepo.findByIdBusiness_IdBusinessAndAccidentDateGreaterThanEqualAndAccidentDateLessThan(biz.toUpperCase(), start, end);

        //El resultado es: Clave: Año y mes del accidente; Valor: Cantidad de accidentes que sucedieron ESE mes
        Map<YearMonth, Long> grouped = accs.stream().collect(Collectors.groupingBy(a -> YearMonth.from(a.getAccidentDate()), Collectors.counting())); //Extraemos la fecha de cada accidente y el counting es un contador que aplica para cada grupo

        //Creamos un arreglo donde se guardarán los elementos finales a mostrar en el gráfico
        List<DTOAccidentMonthCount> data = new ArrayList<>();
        //Se aplica un bucle que por cada mes del periodo solicitado se va a mandar la información del rango de fechas solicitado, incluyendo los meses donde no hubo accidentes
        for (int i=0; i<months; i++){
            YearMonth ym = startYm.plusMonths(i); //Calculamos el mes actual en el bucle sumando 1 para procesar todos los meses en el rango
            data.add(new DTOAccidentMonthCount(ym.toString(), grouped.getOrDefault(ym, 0L))); //Se añade un nuevo elemento y se configura para ser manejado como cadena de texto ej. "2025-01"
        }   //Si no existe devuelve un 0 largo por defecto asegurando que el gráfico no tenga vacíos
        return data; //Mandamos la info al controller
    }

    //Áreas de menor/mayor riesgo
    public DTORiskAreasExtremes riskAreas(String biz){
        //Primero obtenemos todas las locaciones de la empresa
        List<EntityLocation> locations = locRepo.findByIdBusiness_IdBusiness(biz.toUpperCase()); //Con uppercase para evitar confusiones case sensitive
        if (locations.isEmpty()) return new DTORiskAreasExtremes(null, null); //Si no se encontró ninguna locación vamos a devolver null en las locaciones

        //Solicitamos todos los accidentes de la empresa
        var accidents = accRepo.findByIdBusiness_IdBusiness(biz.toUpperCase());

        //Creamos un mapa donde vamos a guardar una colección de las locaciones por su ID agrupandolos por locación e inicia un contador de accidentes por locación
        Map<String, Long> counts = accidents.stream().collect(Collectors.groupingBy(a -> a.getIdLocation().getLocationName(), Collectors.counting()));

        //Creamos dos obj DTO personalizado que vamos a mandar al dashboard
        DTORiskArea min=null, max=null;

        //Por cada locación dentro de la lista locations...
        for (EntityLocation l : locations){
            //Va a busacar todos los elementos que estén asociados con esa locación
            long total = counts.getOrDefault(l.getIdLocation(), 0L);
            DTORiskArea cur = new DTORiskArea(l.getIdLocation(), l.getLocationName(), total);
            if (min==null || cur.getTotal() < min.getTotal()) min = cur; //Definimos la locación con menos elementos contados
            if (max==null || cur.getTotal() > max.getTotal()) max = cur; //Definimos la locación con más elementos contados
        }
        return new DTORiskAreasExtremes(min, max); //Devolvemos el área más peligrosa y la menos peligrosa
    }

    //Calificaciones por capacitación
    public List<DTOTrainingRatings> ratingsAllTrainings(String idBusiness){
        //Buscamos todos los empleados que están dentro de capacitaciones dentro de la empresa
        List<EntityTrainingEmployee> entityTrainingEmployeeList = teRepo.findByIdBusiness_IdBusiness(idBusiness.toUpperCase());
        if (entityTrainingEmployeeList.isEmpty()) return List.of(); //Si no hay ningún empleado dentro de ninguna capacitación va a

        //Mapeado por cada capacitación, donde se va a separar cada capacitación con sus calificaciones
        Map<String, DTOTrainingRatings> trainingRatingsMap = new LinkedHashMap<>();

        //Por cada empleado en capacitación dentro de todos los empleados que pertenecen a una capacitación...
        for (EntityTrainingEmployee te : entityTrainingEmployeeList) {
            //Obtenemos el id de la capacitación
            EntityTraining tr = te.getIdTraining();

            String tName = tr.getTitle(); //Obtenemos el nombre de la capacitación

            //computeIfAbsent permite un manejo eficiente que está disponible en mapas
            //En este caso verifica si la capacitación ya existe en el mapa,
            trainingRatingsMap.computeIfAbsent(tr.getIdTraining(), id -> { //Contendrá un ID Training,
                DTOTrainingRatings d = new DTOTrainingRatings(); //Si no existe la capacitación dentro del mapa va a mostrar
                d.setIdTraining(tr.getIdTraining());
                d.setTrainingName(tName);
                return d;
            });
            //Si ya existe simplemente va a recuperar el valor existente
        }

        //Solicitamos las calificaciones que han registrado los empleados de las capacitaciones
        //Extrayendo sus IDs en una lista
        List<String> employeesIds = entityTrainingEmployeeList.stream() //Mandamos a llamar la lista de entidades
                .map(t -> t.getIdTrainingEmployee()) //Donde vamos a guardar el ID de cada empleado en la capacitación
                .toList(); //Y lo convertimos a lista

        //Solicitamos las calificaciones de que pertenezcan a una misma empresa y sean por parte de empleados que recién guardamos en la lista
        List<EntityTrainingRating> ratings = ratingRepo.findByIdBusiness_IdBusinessAndIdTrainingEmployee_IdTrainingEmployeeIn(idBusiness.toUpperCase(), employeesIds);

        //Recorremos cada calificacione de todas las calificaciones que hemos guardado
        for (EntityTrainingRating rating : ratings) {
            var idTraining = rating.getIdTrainingEmployee().getIdTraining().getIdTraining(); //Obtenemos ID de la capacitación
            DTOTrainingRatings dto = trainingRatingsMap.get(idTraining); //Del mapa vamos a recuperar/obtener el contenido de esa capacitación

            int score = rating.getRatingTraining(); //Tomamos la calificación que mandó el empleado
            switch (score) {
                case 5 -> dto.setStars5(dto.getStars5() + 1);
                case 4 -> dto.setStars4(dto.getStars4() + 1);
                case 3 -> dto.setStars3(dto.getStars3() + 1);
                case 2 -> dto.setStars2(dto.getStars2() + 1);
                default -> dto.setStars1(dto.getStars1() + 1);
            }
            dto.setTotal(dto.getTotal() + 1);
        }

        //Sacamos promedio por capacitación
        for (DTOTrainingRatings allRatings : trainingRatingsMap.values()) {
            //Pero antes debemos verificar que haya al menos una calificación, porque el mínimo es 1
            if (allRatings.getTotal() > 0) {
                //Se realiza una suma donde se le dá prioridad a las calificaciones más altas, también se le puede llamar ponderada
                double sum = allRatings.getStars5()*5 + allRatings.getStars4()*4 + allRatings.getStars3()*3 + allRatings.getStars2()*2 + allRatings.getStars1();

                //Promedio será igual a suma / total redondeado a 1 decimal
                allRatings.setAverage(Math.round((sum / allRatings.getTotal()) * 10.0) / 10.0);
            } else {
                //Si no hay calificaciones el promedio será 0.0
                allRatings.setAverage(0.0);
            }
        }
        //Por último se devuelve la lista de todos los DTOs de las calificaciones
        return new ArrayList<>(trainingRatingsMap.values());
    }
}