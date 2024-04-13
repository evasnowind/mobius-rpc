package com.prayerlaputa.mobiusrpc.core.filter;

import com.prayerlaputa.mobiusrpc.core.api.Filter;
import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;
import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:
 * @description:
 * @date: 2024/4/13 19:31
 * @version: 1.0
 */
public class CacheFilter implements Filter {

    // 替换成guava cache，加容量和过期时间 todo 71
    static Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object prefilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result)  {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
