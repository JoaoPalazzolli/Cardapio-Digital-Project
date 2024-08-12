package br.com.majo.trackingservice.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TrackingNotFoundException extends RuntimeException{

    public TrackingNotFoundException(String msg){
        super(msg);
    }
}
