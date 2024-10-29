package qiwi.test_task.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import qiwi.test_task.dto.ErrorMessageDTO;
import qiwi.test_task.exception.InvalidWalletRequestException;
import qiwi.test_task.exception.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidWalletRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidWalletRequestException(
            InvalidWalletRequestException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleNotFoundException(
            NotFoundException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getMessage()),
                HttpStatus.NOT_FOUND);

    }
}
