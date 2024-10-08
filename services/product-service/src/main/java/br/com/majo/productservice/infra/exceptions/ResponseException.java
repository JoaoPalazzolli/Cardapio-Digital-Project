package br.com.majo.productservice.infra.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ResponseException {

    private String message;
    private LocalDateTime timestamp;
    private String detail;
}
