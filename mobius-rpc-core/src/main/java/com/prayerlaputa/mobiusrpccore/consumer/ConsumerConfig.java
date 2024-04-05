package com.prayerlaputa.mobiusrpccore.consumer;

import com.prayerlaputa.mobiusrpccore.api.LoadBalancer;
import com.prayerlaputa.mobiusrpccore.api.RegistryCenter;
import com.prayerlaputa.mobiusrpccore.api.Router;
import com.prayerlaputa.mobiusrpccore.cluster.RoundRibonLoadBalancer;
import com.prayerlaputa.mobiusrpccore.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class ConsumerConfig {

    @Value("${mobius-rpc.providers}")
    String servers;

    @Bean
    ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            System.out.println("consumer bootstrap starting ...");
            consumerBootstrap.start();
            System.out.println("consumer bootstrap started ...");
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
        //return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
//        return new RegistryCenter.StaticRegistryCenter(List.of(servers.split(",")));
        return new ZkRegistryCenter();
    }


}
