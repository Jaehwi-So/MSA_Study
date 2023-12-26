package com.example.orderservice.controller;


import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
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
@RequestMapping("/order-service")
@AllArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/health-check")
    public String status(){
        return String.format("It's Working in order service on PORT %s", env.getProperty("local.server.port"));
    }


    /* order-service/{user_id}/orders/ */
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createUser(@RequestBody RequestOrder order, @PathVariable("userId") String userId){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);
        orderService.createOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        /* Kafka Messaging Queue에 전달 */
        kafkaProducer.send("example-catalog-topic", orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);  //201 Return

    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrderByUser(@PathVariable("userId") String userId){
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        orderList.forEach(x -> {
            result.add(mapper.map(x, ResponseOrder.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResponseOrder> getOrderByOrderId(@PathVariable("orderId") String orderId){
        OrderDto order = orderService.getOrderByOrderId(orderId);
        ModelMapper mapper = new ModelMapper();
        ResponseOrder result = mapper.map(order, ResponseOrder.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
