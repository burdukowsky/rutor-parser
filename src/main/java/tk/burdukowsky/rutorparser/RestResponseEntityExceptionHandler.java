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
    protected ResponseEntity<ApiError> handleBadGatewayException() {
        return new ResponseEntity<>(
                new ApiError("Ошибка запроса к " + config.getRutorDomain()),
                HttpStatus.BAD_GATEWAY
        );
    }

    @ExceptionHandler(ParseException.class)
    protected ResponseEntity<ApiError> handleParseException() {
        return new ResponseEntity<>(
                new ApiError("Ошибка синтаксического разбора ответа от " + config.getRutorDomain()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
