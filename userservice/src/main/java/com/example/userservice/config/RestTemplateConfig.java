package com.example.userservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced //Spring Cloud Namespace로 접근 가능하도록 하기 위해서
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
