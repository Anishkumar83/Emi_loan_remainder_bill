package org.emiloanwithbill.service.serviceimplementation;

import org.emiloanwithbill.dao.UserDao;
import org.emiloanwithbill.model.User;
import org.emiloanwithbill.service.UserService;
import org.emiloanwithbill.util.PasswordUtil;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void register(User user) {
        if (userDao.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        userDao.insertUser(user);
    }
}
