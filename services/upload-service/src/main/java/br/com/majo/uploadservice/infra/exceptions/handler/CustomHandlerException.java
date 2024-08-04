package br.com.majo.uploadservice.infra.exceptions.handler;

import br.com.majo.uploadservice.infra.exceptions.ResponseException;
import br.com.majo.uploadservice.infra.exceptions.UploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomHandlerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> allExceptionsHandler(WebRequest request, Exception ex){

        var responseException = ResponseException.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .detail(request.getDescription(false))
                .build();

        return new ResponseEntity<>(responseException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<?> uploadExceptionHandler(WebRequest request, Exception ex){

        var responseException = ResponseException.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .detail(request.getDescription(false))
                .build();

        return new ResponseEntity<>(responseException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
