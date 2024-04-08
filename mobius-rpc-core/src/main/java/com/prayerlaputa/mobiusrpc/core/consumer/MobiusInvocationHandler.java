package com.prayerlaputa.mobiusrpc.core.consumer;

import com.prayerlaputa.mobiusrpc.core.api.RpcContext;
import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;
import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;
import com.prayerlaputa.mobiusrpc.core.consumer.http.OkHttpInvoker;
import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.util.MethodUtils;
import com.prayerlaputa.mobiusrpc.core.util.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class MobiusInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    List<InstanceMeta> providers;

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public MobiusInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clazz;
        this.context = context;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            // 解决userService实例调用toString等 方法时也调用服务端的问题。
           return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);

        System.out.println("loadBalancer.choose(instances) ==> " + instance);

        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        } else {
            throw new RuntimeException(rpcResponse.getEx());
        }
    }
}
