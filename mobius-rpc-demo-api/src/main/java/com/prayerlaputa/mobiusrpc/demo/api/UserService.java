package com.prayerlaputa.mobiusrpc.demo.api;

public interface UserService {

    User findById(int id);

//    User findById(long id);

    int getId(int id);

    int getId(User user);

    String getName();

    User findById(int id, String name);

}
