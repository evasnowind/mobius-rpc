package com.prayerlaputa.mobiusrpccore.provider;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpccore.api.RpcRequest;
import com.prayerlaputa.mobiusrpccore.api.RpcResponse;
import com.prayerlaputa.mobiusrpccore.meta.ProviderMeta;
import com.prayerlaputa.mobiusrpccore.util.MethodUtils;
import com.prayerlaputa.mobiusrpccore.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Description:
 * @author: yuchenglong05
 * @create: 2024-03-09 22:48:10
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {


    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @PostConstruct  // init-method
    // PreDestroy
    public void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MobiusProvider.class);
        System.out.println("provider size=" + providers.size());
        providers.forEach((x,y) -> System.out.println(x));

        providers.values().forEach(
                x -> genInterface(x)
        );

    }

    private void genInterface(Object x) {
        Class<?> interfaceClazz = x.getClass().getInterfaces()[0];
        Method[] methods = interfaceClazz.getMethods();
        for (Method method : methods) {
            if(MethodUtils.checkLocalMethod(method)) {
                // 解决userService实例调用toString等 方法时也调用服务端的问题。
                continue;
            }
            createProviders(interfaceClazz, x, method);
        }
    }


    public RpcResponse invoke(RpcRequest request) {

        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            if(meta == null) {
                rpcResponse.setEx(new RuntimeException("can't find ProviderMeta for request[" + request + "]"));
                return rpcResponse;
            }
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            // 方案1：抛出详细信息  rpcResponse.setEx(e);
//            rpcResponse.setEx(e);
            // 方案2：抛出简要信息
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    // 老版本，已废弃。
//    private Method findMethod(Class<?> aClass, String methodName) {
//        for (Method method : aClass.getMethods()) {
//            if(method.getName().equals(methodName)) {  // 有多个重名方法，
//                return method;
//            }
//        }
//        return null;
//    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if(args.length == 0) return args;
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
//            if(args[i] instanceof JSONObject jsonObject) {
//                actuals[i] = jsonObject.toJavaObject(parameterTypes[i]);
//            } else {
//                actuals[i] = args[i];
//            }
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);

        }
        return actuals;
    }

    private void createProviders(Class<?> itfer, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setServiceImpl(x);
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("ProviderMeta: " + meta);
        skeleton.add(itfer.getCanonicalName(), meta);
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optional = providerMetas.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }
}
