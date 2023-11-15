package com.example.apigatewayservice.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
//AbstractGatewayFilterFactory를 상속받음.
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {
            //server.reactive.ServerHttpRequest -> RxJava -> Webflux를 지원해줌
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE Filter: request id -> {}", request.getId());

            //Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST Filter: response code -> {}", response.getStatusCode());
            }));
        };
    }

    public static class Config{
        //Put the configuration properties
    }
}
