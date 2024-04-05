package com.prayerlaputa.mobiusrpccore.api;

import lombok.Data;


@Data
public class RpcRequest {

    private String service; // 接口：com.prayerlaputa.mobius.rpc.demo.api.UserService
    private String methodSign;  // 方法：findById
    private Object[] args;  // 参数： 100

}
