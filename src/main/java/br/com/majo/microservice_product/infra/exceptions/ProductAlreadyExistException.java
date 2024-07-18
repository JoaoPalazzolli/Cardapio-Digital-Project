package br.com.majo.microservice_product.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductAlreadyExistException extends RuntimeException{

    public ProductAlreadyExistException(String msg){
        super(msg);
    }
}
