package br.com.majo.gateway_service.infra.config;

import br.com.majo.gateway_service.infra.utils.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Autowired
    private Routes routes;

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route(p -> p.path(routes.getEndpoint1())
                        .uri(routes.getUri1()))
                .route(p -> p.path(routes.getEndpoint2())
                        .uri(routes.getUri2()))
                .route(p -> p.path(routes.getEndpoint3())
                        .uri(routes.getUri3()))
                .build();
    }
}
