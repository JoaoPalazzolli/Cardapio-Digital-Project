package br.com.majo.categoryservice.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(String msg){
        super(msg);
    }
}
