package com.prayerlaputa.mobiusrpc.core.provider;

import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;
import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;
import com.prayerlaputa.mobiusrpc.core.meta.ProviderMeta;
import com.prayerlaputa.mobiusrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author:
 * @description:
 * @date: 2024/4/6 20:44
 * @version: 1.0
 */
public class ProviderInvoker {


    private MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
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

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if(args == null || args.length == 0) return args;
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        return actuals;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optional = providerMetas.stream()
                .filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }
}
