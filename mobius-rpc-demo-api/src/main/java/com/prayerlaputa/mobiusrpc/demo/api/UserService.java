package com.prayerlaputa.mobiusrpc.demo.api;

public interface UserService {

    User findById(int id);

//    User findById(long id);

    long getId(long id);

    long getId(User user);

    int[] getIds();

    int[] getIds(int[] ids);

    long[] getLongIds();

    String getName();

    User findById(int id, String name);

}
