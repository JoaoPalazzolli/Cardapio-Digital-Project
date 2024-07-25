package br.com.majo.upload_service.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadException extends RuntimeException{

    public UploadException(String msg){
        super(msg);
    }
}
