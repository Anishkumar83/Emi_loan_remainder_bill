package org.emiloanwithbill.service.serviceimplementation;

import org.emiloanwithbill.annotations.PasswordValidator;
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
        try {
            PasswordValidator.validate(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (userDao.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        user.setRole(user.getRole());
        userDao.insertUser(user);
    }

    @Override
    public User login(String username, String password) {

        User dbUser = userDao.findByUsername(username);

        if (dbUser == null) {
            return null;
        }

        boolean match = PasswordUtil.verifyPassword(password, dbUser.getPassword());

        if (!match) {
            return null;
        }

        return dbUser;
    }
}
