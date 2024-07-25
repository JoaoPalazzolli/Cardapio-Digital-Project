package br.com.majo.category_microservice.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistException extends RuntimeException{

    public CategoryAlreadyExistException(String msg){
        super(msg);
    }
}
