package com.example.userservice.client;


import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);

    //잘못된 URL인 경우
    @GetMapping("/order-service/{userId}/orders_ng2")
    List<ResponseOrder> getOrdersNg(@PathVariable String userId);
}
