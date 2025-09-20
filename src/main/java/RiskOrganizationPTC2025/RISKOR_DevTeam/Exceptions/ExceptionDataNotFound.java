package RiskOrganizationPTC2025.RISKOR_DevTeam.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) //Excepción encargada de PROPORCIONAR y lanzar errores de datos que no han sido encontrados (global)
public class ExceptionDataNotFound extends RuntimeException {
    public ExceptionDataNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
