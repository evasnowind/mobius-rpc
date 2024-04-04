package com.prayerlaputa.mobiusrpc.demo.consumer;

import com.prayerlaputa.mobiusrpc.demo.api.OrderService;
import com.prayerlaputa.mobiusrpc.demo.api.User;
import com.prayerlaputa.mobiusrpc.demo.api.Order;
import com.prayerlaputa.mobiusrpc.demo.api.UserService;
import com.prayerlaputa.mobiusrpccore.annotation.MobiusConsumer;
import com.prayerlaputa.mobiusrpccore.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class MobiusRpcDemoConsumerApplication {

    @MobiusConsumer
    UserService userService;
    @MobiusConsumer
    OrderService orderService;
    @Autowired
    private Demo2 demo2;

    public static void main(String[] args) {
        SpringApplication.run(MobiusRpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);

            Order order = orderService.findById(2);
            System.out.println("RPC result orderService.findById(2) = " + order);

            // 模拟异常场景
//            order = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order);

            demo2.test();

            System.out.println("RPC result orderService.getId() = " + userService.getId(11));

            System.out.println("RPC result orderService.getName() = " + userService.getName());
//            System.out.println(userService.toString());
        };
    }
}
