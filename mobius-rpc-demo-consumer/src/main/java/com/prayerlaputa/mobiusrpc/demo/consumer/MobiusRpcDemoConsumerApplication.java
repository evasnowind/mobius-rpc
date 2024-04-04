package com.prayerlaputa.mobiusrpc.demo.consumer;

import com.prayerlaputa.mobiusrpc.demo.api.OrderService;
import com.prayerlaputa.mobiusrpc.demo.api.User;
import com.prayerlaputa.mobiusrpc.demo.api.UserService;
import com.prayerlaputa.mobiusrpccore.annotation.MobiusConsumer;
import com.prayerlaputa.mobiusrpccore.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // 正常调用
//            User user = userService.findById(1);
//            System.out.println("RPC result userService.findById(1) = " + user);

//            Order order = orderService.findById(2);
//            System.out.println("RPC result orderService.findById(2) = " + order);

            // 模拟异常场景
//            order = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order);

            // 测试另一种写法
//            demo2.test();

            // 测试基本数据类型：int
//            System.out.println("RPC result orderService.getId() = " + userService.getId(11));
            // 测试数据类型：String
//            System.out.println("RPC result orderService.getName() = " + userService.getName());

            // 测试toString
//            System.out.println(userService.toString());

            // 测试方法名重载
//            System.out.println("RPC result userService.findById() = " + userService.findById(1, "test"));
//            System.out.println("RPC result userService.getName() = " + userService.getId(144));
//            System.out.println("RPC result userService.getName() = " + userService.getId(new User(123, "tmpObj")));

            // 不同类型参数的支持
//            System.out.println("RPC result userService.getId() = " + userService.getId(144L));
//            System.out.println("RPC result userService.getId() = " + userService.getId(new User(555, "test3")));
            System.out.println("RPC result userService.getIds() = " + Arrays.toString(userService.getIds()));
            System.out.println(" ===> userService.getLongIds()");
            for (long id : userService.getLongIds()) {
                System.out.println(id);
            }

            System.out.println(" ===> userService.getLongIds()");
            for (long id : userService.getIds(new int[]{4,5,6})) {
                System.out.println(id);
            }

            // 测试参数和返回值都是List类型
            List<User> list = userService.getList(List.of(
                    new User(100, "test100"),
                    new User(101, "test101")));
            list.forEach(System.out::println);

            // 测试参数和返回值都是Map类型
            Map<String, User> map = new HashMap<>();
            map.put("A200", new User(200, "test200"));
            map.put("A201", new User(201, "test201"));
            userService.getMap(map).forEach(
                    (k,v) -> System.out.println(k + " -> " + v)
            );
        };
    }
}
