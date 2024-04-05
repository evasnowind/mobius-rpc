package com.prayerlaputa.mobiusrpccore.consumer;

import com.prayerlaputa.mobiusrpccore.api.LoadBalancer;
import com.prayerlaputa.mobiusrpccore.api.Router;
import com.prayerlaputa.mobiusrpccore.cluster.RoundRibonLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {

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

}
