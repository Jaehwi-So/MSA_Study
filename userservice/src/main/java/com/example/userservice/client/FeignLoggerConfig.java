package com.example.userservice.client;


import feign.Logger;
import org.springframework.context.annotation.Configuration;

//FeignErrorDecoder로 대체하여 처리하기
//@Configuration
public class FeignLoggerConfig {

    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
