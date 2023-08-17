package VideoWatch.Exceptions;

    import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(value = {Exception.class})
        public ResponseEntity<Object> handleGenericException(Exception ex) {
            // Log the full stack trace of the error
            ex.printStackTrace();

            // You can replace the message with a more user-friendly one
            String message = ex.getMessage();

            // Create a response containing the error message and HTTP status code
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


