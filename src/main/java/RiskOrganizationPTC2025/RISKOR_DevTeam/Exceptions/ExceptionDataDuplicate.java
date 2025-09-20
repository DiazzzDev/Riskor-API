package RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) //Indicaremos que esta clase será utilizada para DATOS DUPLICADOS (depende de la lógica del negocio)
public class ExceptionDataDuplicate extends RuntimeException {

    @Getter
    private final String duplicateData;

    public ExceptionDataDuplicate(String message, String duplicateData) {
        super(message);
        this.duplicateData = duplicateData;
    }

    public ExceptionDataDuplicate(String duplicateData) {
        this.duplicateData = duplicateData;
    }
}
