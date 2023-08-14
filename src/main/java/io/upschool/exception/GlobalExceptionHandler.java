package io.upschool.exception;

import io.upschool.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyDeletedException.class)
    public ResponseEntity<Object> handleResourceAlreadyDeletedException(ResourceAlreadyDeletedException ex,
                                                                        WebRequest request) {
        // Handle ResourceAlreadyDeletedException
        var response = BaseResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getMessage())
                .isSuccess(false)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                  WebRequest request) {
        var response = BaseResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .isSuccess(false)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        BaseResponse<String> response = BaseResponse.<String>builder()
                .status(400)
                .isSuccess(false)
                .error(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

