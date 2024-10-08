package br.com.majo.restaurantservice.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RestaurantAlreadyExistException extends RuntimeException{

    public RestaurantAlreadyExistException(String msg){
        super(msg);
    }
}
