package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;


    //권한 Config
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("172.30.1.29")   //허용할 IP
                .and()
                .addFilter(getAuthenticationFilter());  //나머지에 대해서는 인증필터 처리


        http.headers().frameOptions().disable();    //H2 Console처럼 HTML Frame 문제 해결

    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        //1. 커스텀 필터 적용
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env);

        //2. Spring Security의 Manager(configure에 설정된 내용)를 필터에 등록.
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    //인증로직 Config
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //userDetailsService을 통해서 loadByUserName을 통해 데이터베이스에서 존재여부를 따진 후 패스워드 인증
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }


}
