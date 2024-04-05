package com.prayerlaputa.mobiusrpccore.consumer;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusConsumer;
import com.prayerlaputa.mobiusrpccore.api.LoadBalancer;
import com.prayerlaputa.mobiusrpccore.api.Router;
import com.prayerlaputa.mobiusrpccore.api.RpcContext;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment environment;

    // consumer一般叫stub；provider一般叫skeleton
    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String urls = environment.getProperty("mobius-rpc.providers", "");
        if(Strings.isEmpty(urls)) {
            System.out.println("mobius-rpc.providers is empty.");
        }
        String[] providers = urls.split(",");

        String[] names = applicationContext.getBeanDefinitionNames();
        for(String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.forEach(
                    f -> {
                        System.out.println(" ===> " + f.getName());
                        Class<?> service = f.getType();
                        String serviceName = service.getCanonicalName();
                        Object consumer = stub.get(serviceName);
                        if (Objects.isNull(consumer)) {
                            consumer = createConsumer(service, context, List.of(providers));
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

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new MobiusInvocationHandler(service, context, providers));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        // 此处一开始拿到的aClass是CGLib增强后的类，此时的子类没有被MobiusConsumer注解修饰，因此起不到作用。

        while(null != aClass) {

            Field[] fields = aClass.getDeclaredFields();
            for(Field f : fields) {
                if (f.isAnnotationPresent(MobiusConsumer.class)) {
                    result.add(f);
                }
            }
            aClass = aClass.getSuperclass();
        }


        return result;
    }
}
