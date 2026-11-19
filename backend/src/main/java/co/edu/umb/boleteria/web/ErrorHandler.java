package co.edu.umb.boleteria.web;
import co.edu.umb.boleteria.web.excepciones.*;import jakarta.validation.ConstraintViolationException;import org.springframework.dao.*;import org.springframework.http.*;import org.springframework.transaction.CannotCreateTransactionException;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestControllerAdvice public class ErrorHandler {
 @ExceptionHandler(NoEncontradaException.class) ResponseEntity<?> no(){return ResponseEntity.status(404).body(Map.of("error","NO_ENCONTRADA"));}
 @ExceptionHandler(SinDisponibilidadException.class) ResponseEntity<?> conflicto(){return ResponseEntity.status(409).body(Map.of("error","SIN_DISPONIBILIDAD"));}
 @ExceptionHandler({PessimisticLockingFailureException.class,TransientDataAccessException.class,CannotCreateTransactionException.class}) ResponseEntity<?> ocupado(){return ResponseEntity.status(503).header(HttpHeaders.RETRY_AFTER,"1").body(Map.of("error","SERVICIO_OCUPADO"));}
 @ExceptionHandler({MethodArgumentNotValidException.class,ConstraintViolationException.class,org.springframework.http.converter.HttpMessageNotReadableException.class}) ResponseEntity<?> validacion(){return ResponseEntity.badRequest().body(Map.of("error","VALIDACION"));}
}
