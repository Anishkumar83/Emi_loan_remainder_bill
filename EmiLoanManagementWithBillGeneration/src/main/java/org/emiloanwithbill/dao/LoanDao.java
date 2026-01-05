package org.emiloanwithbill.dao;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanDao.class);


    private static final int CUSTOMER_ID_INDEX = 1;
    private static final int PRINCIPAL_INDEX = 2;
    private static final int INTEREST_RATE_INDEX = 3;
    private static final int TENURE_INDEX = 4;
    private static final int LOAN_ID_INDEX = 5;

    private static final int GET_LOAN_ID_INDEX = 1;
    private static final int DELETE_LOAN_ID_INDEX = 1;


    private static final String INSERT = """
        INSERT INTO loan ("customerId", "principal", "interestRate", "tenureMonths")
        VALUES (?, ?, ?, ?)
        """;

    private static final String SELECT_ALL = """
        SELECT "loanId", "customerId", "principal", "interestRate", "tenureMonths"
        FROM loan
        """;

    private static final String SELECT_BY_ID = """
        SELECT "loanId", "customerId", "principal", "interestRate", "tenureMonths"
        FROM loan
        WHERE "loanId" = ?
        """;

    private static final String UPDATE = """
        UPDATE loan SET
            "customerId" = ?,
            "principal" = ?,
            "interestRate" = ?,
            "tenureMonths" = ?
        WHERE "loanId" = ?
        """;

    private static final String DELETE = """
        DELETE FROM loan WHERE "loanId" = ?
        """;


    public void insert(Connection con, Loan loan) {
        LOGGER.info("Inside insert loan dao");

        try (PreparedStatement ps =
                     con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(CUSTOMER_ID_INDEX, loan.getCustomerId());
            ps.setDouble(PRINCIPAL_INDEX, loan.getPrincipal());
            ps.setDouble(INTEREST_RATE_INDEX, loan.getInterestRate());
            ps.setInt(TENURE_INDEX, loan.getTenureMonths());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DataException("Loan insert failed");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    loan.setLoanId(rs.getLong(1));
                } else {
                    throw new DataException("Failed to fetch generated loanId");
                }
            }

            LOGGER.info("Loan inserted successfully with loanId={}",
                    loan.getLoanId());

        } catch (SQLException e) {
            throw new DataException("Loan insert failed", e);
        }
    }



    public List<Loan> getAll() {
        LOGGER.info("Inside getAll loans dao");
        List<Loan> loans = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getLong("loanId"));
                loan.setCustomerId(rs.getLong("customerId"));
                loan.setPrincipal(rs.getDouble("principal"));
                loan.setInterestRate(rs.getDouble("interestRate"));
                loan.setTenureMonths(rs.getInt("tenureMonths"));

                loans.add(loan);
            }

            LOGGER.info("Loans retrieved successfully");

        } catch (SQLException e) {
            throw new DataException("Get all loans failed", e);
        }
        return loans;
    }


    public Loan getByLoanId(Connection con,Long loanId) {
        LOGGER.info("Inside getByLoanId dao");

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {

            ps.setLong(GET_LOAN_ID_INDEX, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getLong("loanId"));
                loan.setCustomerId(rs.getLong("customerId"));
                loan.setPrincipal(rs.getDouble("principal"));
                loan.setInterestRate(rs.getDouble("interestRate"));
                loan.setTenureMonths(rs.getInt("tenureMonths"));
                return loan;
            }

        } catch (SQLException e) {
            throw new DataException("Get loan by id failed " + loanId, e);

        }
        return null;
    }


    public void update(Loan loan) {
        LOGGER.info("Inside update loan dao");
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {

            ps.setLong(CUSTOMER_ID_INDEX, loan.getCustomerId());
            ps.setDouble(PRINCIPAL_INDEX, loan.getPrincipal());
            ps.setDouble(INTEREST_RATE_INDEX, loan.getInterestRate());
            ps.setInt(TENURE_INDEX, loan.getTenureMonths());
            ps.setLong(LOAN_ID_INDEX, loan.getLoanId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOGGER.warn("No loan found with id={}", loan.getLoanId());
            } else {
                LOGGER.info("Loan updated successfully id={}", loan.getLoanId());
            }

        } catch (SQLException e) {
            throw new DataException("Loan update failed " + loan.getLoanId(), e);
        }
    }


    public void delete(Long loanId) {
        LOGGER.info("Inside delete loan dao");
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE)) {

            ps.setLong(DELETE_LOAN_ID_INDEX, loanId);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                LOGGER.info("No loan found with id={}", loanId);
            } else {
                LOGGER.info("Loan deleted successfully id={}", loanId);
            }

        } catch (SQLException e) {
            throw new DataException("Loan delete failed " + loanId, e);
        }
    }
}
