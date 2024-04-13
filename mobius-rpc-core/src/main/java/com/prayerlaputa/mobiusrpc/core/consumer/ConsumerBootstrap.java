package com.prayerlaputa.mobiusrpc.core.consumer;

import com.prayerlaputa.mobiusrpc.core.annotation.MobiusConsumer;
import com.prayerlaputa.mobiusrpc.core.api.*;
import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.meta.ServiceMeta;
import com.prayerlaputa.mobiusrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 消费者启动类.
 */
@Slf4j
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    ApplicationContext applicationContext;
    Environment environment;

    // consumer一般叫stub；provider一般叫skeleton
    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);

        String[] names = applicationContext.getBeanDefinitionNames();
        for(String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), MobiusConsumer.class);
            fields.forEach(
                    f -> {
                        log.info(" ===> " + f.getName());
                        Class<?> service = f.getType();
                        String serviceName = service.getCanonicalName();
                        Object consumer = stub.get(serviceName);
                        if (Objects.isNull(consumer)) {
                            consumer = createFromRegistry(service, context, rc);
                            stub.put(serviceName, consumer);
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
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service.getCanonicalName())
                .build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        log.info(" ===> map to providers: ");
        providers.forEach(System.out::println);

        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });
        return createConsumer(service, context, providers);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new MobiusInvocationHandler(service, context, providers));
    }

}
