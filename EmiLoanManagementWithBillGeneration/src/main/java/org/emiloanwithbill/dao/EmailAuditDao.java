package org.emiloanwithbill.dao;

import org.emiloanwithbill.config.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EmailAuditDao {

    private static final String INSERT_AUDIT = """
        INSERT INTO email_audit 
        ("emiId", "customerId", "email", "subject", "status", "error_message")
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    public void saveAudit(long emiId, long customerId, String email,
                          String subject, String status, String errorMsg) {

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_AUDIT)) {

            ps.setLong(1, emiId);
            ps.setLong(2, customerId);
            ps.setString(3, email);
            ps.setString(4, subject);
            ps.setString(5, status);
            ps.setString(6, errorMsg);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
