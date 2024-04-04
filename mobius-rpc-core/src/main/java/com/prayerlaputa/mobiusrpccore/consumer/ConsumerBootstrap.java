package com.prayerlaputa.mobiusrpccore.consumer;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;


@Data
public class ConsumerBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    // consumer一般叫stub；provider一般叫skeleton
    private Map<String, Object> stub = new HashMap<>();


    public void start() {
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
                            consumer = createConsumer(service);
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

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new MobiusInvocationHandler(service));
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
