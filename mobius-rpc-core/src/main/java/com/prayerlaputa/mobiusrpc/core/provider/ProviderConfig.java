package com.prayerlaputa.mobiusrpc.core.provider;

import com.prayerlaputa.mobiusrpc.core.api.RegistryCenter;
import com.prayerlaputa.mobiusrpc.core.registry.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }


    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            log.info("providerBootstrap starting ...");
            /*
            注意：此处将注册操作单独封装一个start方法、并放到ApplicationRunner，是因为如果放到@PostConstruct，此时对象尚未实例化。
            如果未实例化就注册，实际被注册的服务尚未可用，是bug！
             */
            providerBootstrap.start();
            log.info("providerBootstrap started ...");
        };
    }


    @Bean
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}