package org.emiloanwithbill.dao;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.dto.BillCustomerDto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillReminderDao {
    private static final String FETCH_DUE_EMI = """
    SELECT 
        e."emiId",
        c."customerId",
        c."firstName",
        c."email",
        c."dob"
    FROM emi_schedule e
    JOIN loan l ON e."loanId" = l."loanId"
    JOIN customers c ON l."customerId" = c."customerId"
    WHERE e."due_date" = ?
      AND e."status" = 'PENDING'
""";


    public List<BillCustomerDto> fetchCustomersForTomorrow(LocalDate date) {

        List<BillCustomerDto> list = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(FETCH_DUE_EMI)) {

            ps.setDate(1, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BillCustomerDto dto = new BillCustomerDto();
                dto.setEmiId(rs.getLong("emiId"));
                dto.setCustomerId(rs.getLong("customerId"));
                dto.setName(rs.getString("firstName"));
                dto.setEmail(rs.getString("email"));
                dto.setDob(rs.getDate("dob").toLocalDate());

                list.add(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch bill customers", e);
        }

        return list;
    }
}
