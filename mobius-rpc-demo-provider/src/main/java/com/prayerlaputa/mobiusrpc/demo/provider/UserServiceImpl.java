package com.prayerlaputa.mobiusrpc.demo.provider;


import com.prayerlaputa.mobiusrpccore.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpc.demo.api.User;
import com.prayerlaputa.mobiusrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@MobiusProvider
public class UserServiceImpl implements UserService {

    @Autowired
    Environment environment;


    public UserServiceImpl() {
        System.out.println("UserServiceImpl instantiated");
    }

    @Override
    public User findById(int id) {
        return new User(id, "Mobius-" + environment.getProperty("server.port")
                + "_" + System.currentTimeMillis());
    }

    @Override
    public float getId(float id) {
        return id;
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public int[] getIds() {
        return new int[]{1,2,3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }


    @Override
    public long[] getLongIds() {
        return new long[]{1,2,3};
    }

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public String getName(int id) {
        return "name-" + id;
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "Mobius-" + name + "-" + System.currentTimeMillis());
    }

    @Override
    public User[] findUsers(User[] users) {
        return users;
    }

    @Override
    public List<User> getList(List<User> userList) {
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return flag;
    }

}
