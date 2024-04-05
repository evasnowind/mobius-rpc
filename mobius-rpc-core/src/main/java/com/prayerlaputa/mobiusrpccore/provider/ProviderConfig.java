package com.prayerlaputa.mobiusrpccore.provider;

import com.prayerlaputa.mobiusrpccore.api.RegistryCenter;
import com.prayerlaputa.mobiusrpccore.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @Description:
 * @author: yuchenglong05
 * @create: 2024-03-09 22:49:34
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            System.out.println("providerBootstrap starting ...");
            /*
            注意：此处将注册操作单独封装一个start方法、并放到ApplicationRunner，是因为如果放到@PostConstruct，此时对象尚未实例化。
            如果未实例化就注册，实际被注册的服务尚未可用，是bug！
             */
            providerBootstrap.start();
            System.out.println("providerBootstrap started ...");
        };
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}