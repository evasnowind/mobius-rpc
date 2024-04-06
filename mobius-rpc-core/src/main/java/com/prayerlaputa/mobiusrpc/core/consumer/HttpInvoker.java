package com.prayerlaputa.mobiusrpc.core.consumer;

import com.prayerlaputa.mobiusrpc.core.api.RpcResponse;
import com.prayerlaputa.mobiusrpc.core.api.RpcRequest;

public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
