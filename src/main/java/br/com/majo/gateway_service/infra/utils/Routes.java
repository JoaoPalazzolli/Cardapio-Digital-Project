package br.com.majo.gateway_service.infra.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Routes {

    @Value("${gateway.routes.route-1.endpoint}")
    private String endpoint1;
    @Value("${gateway.routes.route-1.uri}")
    private String uri1;
    @Value("${gateway.routes.route-2.endpoint}")
    private String endpoint2;
    @Value("${gateway.routes.route-2.uri}")
    private String uri2;
    @Value("${gateway.routes.route-3.endpoint}")
    private String endpoint3;
    @Value("${gateway.routes.route-3.uri}")
    private String uri3;
}
