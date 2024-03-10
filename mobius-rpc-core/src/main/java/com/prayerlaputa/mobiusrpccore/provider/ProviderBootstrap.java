package com.prayerlaputa.mobiusrpccore.provider;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpccore.api.RpcRequest;
import com.prayerlaputa.mobiusrpccore.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: yuchenglong05
 * @create: 2024-03-09 22:48:10
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {


    ApplicationContext applicationContext;

    private Map<String, Object> skeleton = new HashMap<>();


    @PostConstruct  // init-method
    // PreDestroy
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MobiusProvider.class);
        System.out.println("provider size=" + providers.size());
        providers.forEach((x,y) -> System.out.println(x));
//        skeleton.putAll(providers);

        providers.values().forEach(
                x -> genInterface(x)
        );

    }

    private void genInterface(Object x) {
        Class<?> interfaceClazz = x.getClass().getInterfaces()[0];
        skeleton.put(interfaceClazz.getCanonicalName(), x);
    }


    public RpcResponse invoke(RpcRequest request) {
        Object bean = skeleton.get(request.getService());
        try {
            Method method = findMethod(bean.getClass(), request.getMethod());
            Object result = method.invoke(bean, request.getArgs());
            return new RpcResponse(true, result);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if(method.getName().equals(methodName)) {  // 有多个重名方法，
                return method;
            }
        }
        return null;
    }

}
