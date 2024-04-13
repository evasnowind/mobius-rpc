package com.prayerlaputa.mobiusrpc.core.consumer;

import com.prayerlaputa.mobiusrpc.core.api.Filter;
import com.prayerlaputa.mobiusrpc.core.api.LoadBalancer;
import com.prayerlaputa.mobiusrpc.core.api.RegistryCenter;
import com.prayerlaputa.mobiusrpc.core.api.Router;
import com.prayerlaputa.mobiusrpc.core.cluster.RoundRibonLoadBalancer;
import com.prayerlaputa.mobiusrpc.core.filter.CacheFilter;
import com.prayerlaputa.mobiusrpc.core.filter.MockFilter;
import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.registry.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
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
            log.info("consumer bootstrap starting ...");
            consumerBootstrap.start();
            log.info("consumer bootstrap started ...");
        };
    }

    @Bean
    public LoadBalancer<InstanceMeta>  loadBalancer() {
        //return LoadBalancer.Default;
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router<InstanceMeta>  router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
//        return new RegistryCenter.StaticRegistryCenter(List.of(servers.split(",")));
        return new ZkRegistryCenter();
    }

    @Bean
    public Filter defaultFilter() {
        return Filter.Default;
    }

    @Bean
    public Filter filter() {
        return new CacheFilter();
    }
//
//    @Bean
//    public Filter filter2() {
//        return new MockFilter();
//    }

}
