package com.prayerlaputa.mobiusrpc.core.consumer;

import com.prayerlaputa.mobiusrpc.core.api.Filter;
import com.prayerlaputa.mobiusrpc.core.api.RpcContext;
import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;
import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;
import com.prayerlaputa.mobiusrpc.core.consumer.http.OkHttpInvoker;
import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.util.MethodUtils;
import com.prayerlaputa.mobiusrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
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

        for (Filter filter : this.context.getFilters()) {
            Object preResult = filter.prefilter(rpcRequest);
            if(preResult != null) {
                log.debug(filter.getClass().getName() + " ==> prefilter: " + preResult);
                return preResult;
            }
        }

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);

        log.debug("loadBalancer.choose(instances) ==> " + instance);

        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        Object result = castReturnResult(method, rpcResponse);

        for (Filter filter : this.context.getFilters()) {
            // 此处拿到的可能不是最终值，导致CacheFilter缓存的不是最终结果，有2种思路：
            // 1、filter支持顺序，保证CacheFilter最后执行
            // 2、拿到执行后结果result，CacheFilter可以不处理、仅返回result。下面即展示了这种思路。
            Object filterResult = filter.postfilter(rpcRequest, rpcResponse, result);
            if(filterResult != null) {
                return filterResult;
            }
        }

        return result;
    }


    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }
}
