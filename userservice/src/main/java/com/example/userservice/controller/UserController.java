package com.example.userservice.controller;


import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/user-service")
@AllArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;
    private final Greeting greeting;

    @GetMapping("/health-check")
    public String status(){

        return String.format("It's Working in user service on PORT %s", env.getProperty("local.server.port"));
    }


    @GetMapping("/welcome")
    public String welcome(){
        // return this.env.getProperty("greeting.message");
        return this.greeting.getMessage();
    }

    /* 신규 회원 등록 */
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);  //201 Return

    }

    /* 회원 정보 리스트 조회 */
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        userList.forEach(x -> {
            result.add(mapper.map(x, ResponseUser.class));
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    /* 회원 정보 상세보기 */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        UserDto dto = userService.getUserByUserId(userId);
        ModelMapper mapper = new ModelMapper();
        ResponseUser result = mapper.map(dto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }



}
