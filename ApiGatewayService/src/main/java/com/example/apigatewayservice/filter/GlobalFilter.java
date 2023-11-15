package com.example.apigatewayservice.filter;


import lombok.Data;
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
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            //server.reactive.ServerHttpRequest -> RxJava -> Webflux를 지원해줌
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            //Pre filter
            if(config.isPrelogger()){
                log.info("Global Filter Start: request id -> {}", request.getId());
            }

            //Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()){
                    log.info("Global Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        };
    }

    @Data   //Getter/Setter 적용
    public static class Config{
        private String baseMessage;
        private boolean prelogger;
        private boolean postLogger;
    }
}
