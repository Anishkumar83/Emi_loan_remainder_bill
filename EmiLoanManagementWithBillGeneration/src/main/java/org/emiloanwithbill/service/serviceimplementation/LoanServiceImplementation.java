package org.emiloanwithbill.service.serviceimplementation;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.dao.EmiDao;
import org.emiloanwithbill.dao.LoanDao;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;
import org.emiloanwithbill.service.LoanService;
import org.emiloanwithbill.util.EmiCalculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LoanServiceImplementation implements LoanService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoanServiceImplementation.class);

    private static final int SCALE = 2;

    private final LoanDao loanDao = new LoanDao();
    private final EmiDao emiDao = new EmiDao();

    @Override
    public long createLoan(long customerId,
                           BigDecimal principal,
                           BigDecimal rate,
                           int months) {

        Connection con = null;

        try {
            con = DbConnection.getConnection();
            con.setAutoCommit(false);

            validate(customerId, principal, rate, months);

            Loan loan = new Loan();
            loan.setCustomerId(customerId);
            loan.setPrincipal(principal.doubleValue());
            loan.setInterestRate(rate.doubleValue());
            loan.setTenureMonths(months);

            loanDao.insert(con, loan);

            createEmiSchedule(con,
                    loan.getLoanId(),
                    principal,
                    rate,
                    months);

            con.commit();

            LOGGER.info("Loan created successfully loanId={}",
                    loan.getLoanId());

            return loan.getLoanId();

        } catch (Exception e) {
            rollback(con);
            throw new DataException("Loan could not be created", e);

        } finally {
            close(con);
        }
    }


    private void validate(long customerId,
                          BigDecimal principal,
                          BigDecimal rate,
                          int months) {

        if (customerId <= 0)
            throw new IllegalArgumentException("Invalid customerId");
        if (principal.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Invalid principal");
        if (rate.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Invalid rate");
        if (months <= 0)
            throw new IllegalArgumentException("Invalid tenure");
    }

    private void createEmiSchedule(Connection con,
                                   long loanId,
                                   BigDecimal principal,
                                   BigDecimal rate,
                                   int months) {

        BigDecimal emiAmount =
                EmiCalculation.emiCalculation(principal, rate, months);

        BigDecimal balance = principal;
        BigDecimal monthlyRate =
                rate.divide(BigDecimal.valueOf(1200),
                        10, RoundingMode.HALF_UP);

        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= months; i++) {

            BigDecimal interest =
                    balance.multiply(monthlyRate)
                            .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal principalPaid =
                    emiAmount.subtract(interest)
                            .setScale(SCALE, RoundingMode.HALF_UP);

            balance =
                    balance.subtract(principalPaid)
                            .max(BigDecimal.ZERO);

            Emi emi = new Emi();
            emi.setLoanId(loanId);
            emi.setEmiAmount(emiAmount.doubleValue());
            emi.setInterest_component(interest.doubleValue());
            emi.setPrincipal_component(principalPaid.doubleValue());
            emi.setOutstanding_balance(balance.doubleValue());
            emi.setDueDate(dueDate);
            emi.setStatus("PENDING");

            emiDao.saveEmi(con, emi);
            dueDate = dueDate.plusMonths(1);
        }
    }

    private void rollback(Connection con) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException e) {
            throw new DataException("Rollback failed", e);
        }
    }

    private void close(Connection con) {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            throw new DataException("Close failed", e);
        }
    }

    @Override
    public Loan getLoanById(long loanId) {
        try (Connection con = DbConnection.getConnection()) {
            return loanDao.getByLoanId(con, loanId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch loan", e);
        }
    }

    @Override
    public List<Emi> getEmiSchedule(long loanId) {
        try (Connection con = DbConnection.getConnection()) {
            return emiDao.getEmiByLoanId(con, loanId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch EMI schedule", e);
        }
    }

}
