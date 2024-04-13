package com.prayerlaputa.mobiusrpc.demo.consumer;

import com.prayerlaputa.mobiusrpc.demo.api.User;
import com.prayerlaputa.mobiusrpc.demo.api.UserService;
import com.prayerlaputa.mobiusrpc.core.annotation.MobiusConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Demo2 {
    @MobiusConsumer
    UserService userService2;

    public void test() {
        User user = userService2.findById(100);
        log.info(user.toString());
    }
}
