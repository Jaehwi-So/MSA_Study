package com.example.apigatewayservice.filter;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
//AbstractGatewayFilterFactory를 상속받음.
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter(){
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        //필터의 정의는 이런식으로도 가능
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());

            //Pre filter
            if(config.isPrelogger()){
                log.info("Logging PRE Filter: request id -> {}", request.getId());
            }

            //Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()){
                    log.info("Logging POST Filter: response code -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE);
        // 로그의 순서를 지정할 수 있음(우선순위 지정)

        return filter;
    }


    @Data   //Getter/Setter 적용
    public static class Config{
        private String baseMessage;
        private boolean prelogger;
        private boolean postLogger;
        private String sayHello;
    }
}
