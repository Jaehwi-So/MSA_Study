package com.example.apigatewayservice.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                //First Service 등록
                .route(r -> r.path("/first-service/**") //path 등록
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header") //RequestFilter에 추가
                                .addResponseHeader("first-response", "first-response-header"))  //ResponseFilter에 추가
                        .uri("http://localhost:8081"))  //uri 등록
                //Second Service 등록
                .route(r -> r.path("/second-service/**") //path 등록
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header") //RequestFilter에 추가
                                .addResponseHeader("second-response", "second-response-header"))  //ResponseFilter에 추가
                        .uri("http://localhost:8082"))  //uri 등록
                .build();
    }
}
