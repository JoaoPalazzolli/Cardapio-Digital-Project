package br.com.majo.microservice_product.infra.exceptions.handler;

import br.com.majo.microservice_product.infra.exceptions.ProductAlreadyExistException;
import br.com.majo.microservice_product.infra.exceptions.ProductNotFoundException;
import br.com.majo.microservice_product.infra.exceptions.ResponseException;
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

    @ExceptionHandler(ProductAlreadyExistException.class)
    public ResponseEntity<?> productAlreadyExistExceptionHandler(WebRequest request, Exception ex){

        var responseException = ResponseException.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .detail(request.getDescription(false))
                .build();

        return new ResponseEntity<>(responseException, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> productNotFoundExceptionHandler(WebRequest request, Exception ex){

        var responseException = ResponseException.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .detail(request.getDescription(false))
                .build();

        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }
}
