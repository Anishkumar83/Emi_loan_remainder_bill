package org.emiloanwithbill.dao;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserDao {

    private static final String INSERT_USER =
            "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

    public boolean insertUser(User user) {
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_USER)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error inserting user", e);
        }
    }
}
