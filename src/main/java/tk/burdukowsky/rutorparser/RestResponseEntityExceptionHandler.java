package tk.burdukowsky.rutorparser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Config config;

    public RestResponseEntityExceptionHandler(Config config) {
        this.config = config;
    }

    @ExceptionHandler(BadGatewayException.class)
    protected ResponseEntity<ApiError> handleBadGatewayException(BadGatewayException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.BAD_GATEWAY
        );
    }

    @ExceptionHandler(ParseException.class)
    protected ResponseEntity<ApiError> handleParseException(ParseException e) {
        var message = String.format(
                "Ошибка синтаксического разбора ответа от %s. Причина: %s",
                config.getRutorDomain(),
                e.getMessage()
        );
        return new ResponseEntity<>(new ApiError(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalException.class)
    protected ResponseEntity<ApiError> handleInternalException(InternalException e) {
        return new ResponseEntity<>(new ApiError(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
