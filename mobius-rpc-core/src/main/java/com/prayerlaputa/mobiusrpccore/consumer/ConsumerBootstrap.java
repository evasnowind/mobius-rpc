package com.prayerlaputa.mobiusrpccore.consumer;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusConsumer;
import com.prayerlaputa.mobiusrpccore.api.LoadBalancer;
import com.prayerlaputa.mobiusrpccore.api.RegistryCenter;
import com.prayerlaputa.mobiusrpccore.api.Router;
import com.prayerlaputa.mobiusrpccore.api.RpcContext;
import com.prayerlaputa.mobiusrpccore.util.MethodUtils;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment environment;

    // consumer一般叫stub；provider一般叫skeleton
    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String[] names = applicationContext.getBeanDefinitionNames();
        for(String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), MobiusConsumer.class);
            fields.forEach(
                    f -> {
                        System.out.println(" ===> " + f.getName());
                        Class<?> service = f.getType();
                        String serviceName = service.getCanonicalName();
                        Object consumer = stub.get(serviceName);
                        if (Objects.isNull(consumer)) {
                            consumer = createFromRegistry(service, context, rc);
                        }
                        f.setAccessible(true);
                try {
                    f.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        List<String> providers = mapUrls(rc.fetchAll(serviceName));
        System.out.println(" ===> map to providers: ");
        providers.forEach(System.out::println);

        rc.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrls(event.getData()));
        });
        return createConsumer(service, context, providers);
    }

    private List<String> mapUrls(List<String> nodes) {
        return nodes.stream()
                .map(x -> "http://" + x.replace('_', ':'))
                .collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new MobiusInvocationHandler(service, context, providers));
    }

}
