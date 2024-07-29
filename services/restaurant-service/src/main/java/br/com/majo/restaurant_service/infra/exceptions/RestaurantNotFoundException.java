package br.com.majo.restaurant_service.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RestaurantNotFoundException extends RuntimeException{

    public RestaurantNotFoundException(String msg){
        super(msg);
    }
}
