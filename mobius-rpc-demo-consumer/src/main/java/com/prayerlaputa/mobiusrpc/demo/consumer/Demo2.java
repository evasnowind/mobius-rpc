package com.prayerlaputa.mobiusrpc.demo.consumer;

import com.prayerlaputa.mobiusrpc.demo.api.User;
import com.prayerlaputa.mobiusrpc.demo.api.UserService;
import com.prayerlaputa.mobiusrpccore.annotation.MobiusConsumer;
import org.springframework.stereotype.Component;

@Component
public class Demo2 {
    @MobiusConsumer
    UserService userService2;

    public void test() {
        User user = userService2.findById(100);
        System.out.println(user);
    }
}
