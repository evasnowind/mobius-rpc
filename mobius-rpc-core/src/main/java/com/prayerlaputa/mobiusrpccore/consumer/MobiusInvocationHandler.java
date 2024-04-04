package com.prayerlaputa.mobiusrpccore.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prayerlaputa.mobiusrpccore.api.RpcRequest;
import com.prayerlaputa.mobiusrpccore.api.RpcResponse;
import com.prayerlaputa.mobiusrpccore.util.MethodUtils;
import com.prayerlaputa.mobiusrpccore.util.TypeUtils;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MobiusInvocationHandler implements InvocationHandler {

    final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    Class<?> service;

    public MobiusInvocationHandler(Class<?> clazz) {
        this.service = clazz;
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

        RpcResponse rpcResponse = post(rpcRequest);
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            Class<?> type = method.getReturnType();
            System.out.println("method.getReturnType() = " + type);
            if (data instanceof JSONObject jsonResult) {
                if (Map.class.isAssignableFrom(type)) {
                    Map resultMap = new HashMap();
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println(genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Class<?> keyType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                        Class<?> valueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                        System.out.println("keyType  : " + keyType);
                        System.out.println("valueType: " + valueType);
                        jsonResult.entrySet().stream().forEach(
                                e -> {
                                    Object key = TypeUtils.cast(e.getKey(), keyType);
                                    Object value = TypeUtils.cast(e.getValue(), valueType);
                                    resultMap.put(key, value);
                                }
                        );
                    }
                    return resultMap;
                }
                return jsonResult.toJavaObject(type);
            } else if (data instanceof JSONArray jsonArray) {
                Object[] array = jsonArray.toArray();
                if (type.isArray()) {
                    Class<?> componentType = type.getComponentType();
                    Object resultArray = Array.newInstance(componentType, array.length);
                    for (int i = 0; i < array.length; i++) {
                        Array.set(resultArray, i, array[i]);
                    }
                    return resultArray;
                } else if (List.class.isAssignableFrom(type)) {
                    List<Object> resultList = new ArrayList<>(array.length);
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println(genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Type actualType = parameterizedType.getActualTypeArguments()[0];
                        System.out.println(actualType);
                        for (Object o : array) {
                            resultList.add(TypeUtils.cast(o, (Class<?>) actualType));
                        }
                    } else {
                        resultList.addAll(Arrays.asList(array));
                    }
                    return resultList;
                } else {
                    return null;
                }
            } else {
                return TypeUtils.cast(data, type);
            }
        } else {
            //            ex.printStackTrace();
            throw new RuntimeException(rpcResponse.getEx());
        }
    }

    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectionPool(new ConnectionPool(16,60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println("===> reqJson = " + reqJson);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("===> respJson = " + respJson);
            return JSON.parseObject(respJson, RpcResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
