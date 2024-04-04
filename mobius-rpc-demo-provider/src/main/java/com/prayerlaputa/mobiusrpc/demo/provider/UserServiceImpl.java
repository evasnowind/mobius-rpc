package com.prayerlaputa.mobiusrpc.demo.provider;


import com.prayerlaputa.mobiusrpccore.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpc.demo.api.User;
import com.prayerlaputa.mobiusrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

@Component
@MobiusProvider
public class UserServiceImpl implements UserService {

    public UserServiceImpl() {
        System.out.println("UserServiceImpl instantiated");
    }

    @Override
    public User findById(int id) {
        return new User(id, "Mobius-" + System.currentTimeMillis());
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
    public User findById(int id, String name) {
        return new User(id, "Mobius-" + name + "-" + System.currentTimeMillis());
    }
}
