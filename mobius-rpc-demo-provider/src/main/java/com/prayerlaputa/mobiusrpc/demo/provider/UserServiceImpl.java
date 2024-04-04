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
    public int getId(int id) {
        return id;
    }

    @Override
    public String getName() {
        return "name";
    }
}
