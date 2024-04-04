package com.prayerlaputa.mobiusrpc.demo.api;

import java.util.List;
import java.util.Map;

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

    List<User> getList(List<User> userList);

    Map<String, User> getMap(Map<String, User> userMap);

}
