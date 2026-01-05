package org.emiloanwithbill.dao;


import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Emi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmiDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmiDao.class);



    private static final int LOAN_ID_INDEX = 1;
    private static final int EMI_AMOUNT_INDEX = 2;
    private static final int INTEREST_INDEX = 3;
    private static final int PRINCIPAL_INDEX = 4;
    private static final int OUTSTANDING_INDEX = 5;
    private static final int DUE_DATE_INDEX = 6;
    private static final int STATUS_INDEX = 7;





    private static final String INSERT = """
        INSERT INTO emi_schedule
        ("loanId", "emi_amount", "interest_component",
         "principal_component", "outstanding_balance",
         "due_date", "status")
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SELECT_BY_LOAN_ID = """
        SELECT "emiId", "loanId", "emi_amount",
               "interest_component", "principal_component",
               "outstanding_balance", "due_date", "status"
        FROM emi_schedule
        WHERE "loanId" = ?
        ORDER BY "due_date"
        """;

    private static final String UPDATE_STATUS = """
        UPDATE emi_schedule
        SET "status" = ?
        WHERE "emiId" = ?
        """;


    public void saveEmi(Connection con, Emi emi) {

        LOGGER.debug("Saving EMI for loanId={}", emi.getLoanId());

        try (PreparedStatement ps =
                     con.prepareStatement(INSERT)) {

            ps.setLong(LOAN_ID_INDEX, emi.getLoanId());
            ps.setDouble(EMI_AMOUNT_INDEX, emi.getEmiAmount());
            ps.setDouble(INTEREST_INDEX, emi.getInterest_component());
            ps.setDouble(PRINCIPAL_INDEX, emi.getPrincipal_component());
            ps.setDouble(OUTSTANDING_INDEX, emi.getOutstanding_balance());
            ps.setDate(DUE_DATE_INDEX, Date.valueOf(emi.getDueDate()));
            ps.setString(STATUS_INDEX, emi.getStatus());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DataException("EMI insert failed");
            }

        } catch (SQLException e) {
            throw new DataException("EMI insert failed", e);
        }
    }


    public List<Emi> getEmiByLoanId(Connection con, long loanId) {

        LOGGER.info("Fetching EMI schedule for loanId={}", loanId);
        List<Emi> emis = new ArrayList<>();

        try (PreparedStatement ps =
                     con.prepareStatement(SELECT_BY_LOAN_ID)) {

            ps.setLong(1, loanId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                emis.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DataException("Fetch EMI failed", e);
        }

        return emis;
    }



    public void updateStatus(Connection con,
                             long emiId,
                             String status) {

        LOGGER.info("Updating EMI status emiId={} status={}",
                emiId, status);

        try (PreparedStatement ps =
                     con.prepareStatement(UPDATE_STATUS)) {

            ps.setString(1, status);
            ps.setLong(2, emiId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOGGER.warn("No EMI found with emiId={}", emiId);
            }

        } catch (SQLException e) {
            throw new DataException("Update EMI status failed", e);
        }
    }



    private Emi mapRow(ResultSet rs) throws SQLException {

        Emi emi = new Emi();
        emi.setEmiId(rs.getLong("emiId"));
        emi.setLoanId(rs.getLong("loanId"));
        emi.setEmiAmount(rs.getDouble("emi_amount"));
        emi.setInterest_component(
                rs.getDouble("interest_component"));
        emi.setPrincipal_component(
                rs.getDouble("principal_component"));
        emi.setOutstanding_balance(
                rs.getDouble("outstanding_balance"));
        emi.setDueDate(
                rs.getDate("due_date").toLocalDate());
        emi.setStatus(rs.getString("status"));

        return emi;
    }
}
