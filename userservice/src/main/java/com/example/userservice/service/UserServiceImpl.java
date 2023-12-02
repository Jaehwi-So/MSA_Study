package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment env;

    private final RestTemplate restTemplate;
    private final OrderServiceClient orderServiceClient;


    /**
     * 인증을 위한 상속받은 UserDetailService의 loadByUserName 구현
     * 사용자가 존재하는지 아닌지 판단한다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = this.userRepository.findByEmail(username);
        if(userEntity == null){
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
                //Spring Security의 Object로 반환한다.
                //ArrayList는 권한 리스트(Role)을 반환
    }



    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        return returnUserDto;
    }



    /* Feign Client를 사용한 MSA간 통신 */
    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        UserDto dto = new ModelMapper().map(userEntity, UserDto.class);

        List<ResponseOrder> orderList = null;
        orderList = orderServiceClient.getOrders(userId);

        /* FeignErrorDecoder 사용 전 (FeignLoggerConfig 사용) */
//        try{
//            orderList = orderServiceClient.getOrders(userId);
//        }
//        catch(FeignException ex){
//            log.error(ex.getMessage());
//        }

        dto.setOrders(orderList);

        return dto;
    }

    /* Rest Template을 사용한 MSA간 통신 */
    @Override
    public UserDto getUserByUserIdUseRestTemplate(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        UserDto dto = new ModelMapper().map(userEntity, UserDto.class);


        String orderUrl = String.format(this.env.getProperty("order_service.url"), userId);

        ResponseEntity<List<ResponseOrder>> orderListReponse =
            restTemplate.exchange(orderUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<ResponseOrder>>() {
            });

        List<ResponseOrder> orderList = orderListReponse.getBody();
        dto.setOrders(orderList);

        return dto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity entity = this.userRepository.findByEmail(email);
        if(entity == null){
            throw new UsernameNotFoundException(email);
        }
        UserDto userDto = new ModelMapper().map(entity, UserDto.class);
        return userDto;
    }


}
