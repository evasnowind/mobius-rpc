package com.prayerlaputa.mobiusrpc.core.filter;

import com.prayerlaputa.mobiusrpc.core.api.Filter;
import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;
import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;
import com.prayerlaputa.mobiusrpc.core.util.MethodUtils;
import com.prayerlaputa.mobiusrpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author:
 * @description:
 * @date: 2024/4/13 21:21
 * @version: 1.0
 */
public class MockFilter implements Filter {
    @SneakyThrows
    @Override
    public Object prefilter(RpcRequest request) {
        Class service = Class.forName(request.getService());
        Method method = findMethod(service, request.getMethodSign());
        Class clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    private Method findMethod(Class service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtils.methodSign(method)))
                .findFirst().orElse(null);
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
