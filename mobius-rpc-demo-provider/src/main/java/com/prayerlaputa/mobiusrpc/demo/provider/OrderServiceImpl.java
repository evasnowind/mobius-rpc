package com.prayerlaputa.mobiusrpc.demo.provider;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpc.demo.api.Order;
import com.prayerlaputa.mobiusrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

@Component
@MobiusProvider
public class OrderServiceImpl implements OrderService {


    public OrderServiceImpl() {
        System.out.println("OrderServiceImpl instantiated");
    }

    @Override
    public Order findById(Integer id) {
        if (404 == id) {
            // 模拟异常场景
            throw new RuntimeException("404 exception");
        }
        return new Order(id.longValue(), 15.6f);
    }
}
