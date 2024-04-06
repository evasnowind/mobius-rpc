package com.prayerlaputa.mobiusrpc.demo.provider;

import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;
import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;
import com.prayerlaputa.mobiusrpc.core.provider.ProviderConfig;
import com.prayerlaputa.mobiusrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class MobiusRpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobiusRpcDemoProviderApplication.class, args);
    }

    // 使用HTTP + JSON 来实现序列化和通信

    @Autowired
    ProviderInvoker providerInvoker;

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }


    @Bean
    ApplicationRunner providerRun() {
        return x -> {
            RpcRequest request = new RpcRequest();
            request.setService("com.prayerlaputa.mobiusrpc.demo.api.UserService");
            request.setMethodSign("findById@1_int");
            request.setArgs(new Object[]{100});

            RpcResponse rpcResponse = invoke(request);
            System.out.println("return : "+rpcResponse.getData());

                        // test override method
            request = new RpcRequest();
            request.setService("com.prayerlaputa.mobiusrpc.demo.api.UserService");
            request.setMethodSign("findById@2_int_java.lang.String");
            request.setArgs(new Object[]{100, "test2"});

            rpcResponse = invoke(request);
            System.out.println("return : "+rpcResponse.getData());


        };
    }


}
