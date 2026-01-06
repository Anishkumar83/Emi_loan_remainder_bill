package org.emiloanwithbilltest.service;


import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.dao.EmiDao;
import org.emiloanwithbill.dao.LoanDao;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;
import org.emiloanwithbill.service.serviceimplementation.LoanServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplementationTest {

    @Mock
    private LoanDao loanDao;

    @Mock
    private EmiDao emiDao;

    @Mock
    private Connection connection;

    private LoanServiceImplementation loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImplementation(loanDao, emiDao);
    }

    @Test
    void createLoan_success() throws Exception {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection).thenReturn(connection);

            doNothing().when(connection).setAutoCommit(false);
            doNothing().when(connection).commit();
            doNothing().when(connection).close();

            doAnswer(invocation -> {
                Loan loan = invocation.getArgument(1);
                loan.setLoanId(1L);
                return null;
            }).when(loanDao).insert(eq(connection), any(Loan.class));

            doNothing().when(emiDao).saveEmi(eq(connection), any(Emi.class));

            long loanId = loanService.createLoan(
                    1L,
                    new BigDecimal("100000"),
                    new BigDecimal("10"),
                    12
            );

            assertEquals(1L, loanId);
            verify(connection).setAutoCommit(false);
            verify(connection).commit();
            verify(connection).close();
        }
    }


    @Test
    void createLoan_invalidCustomerId() throws Exception {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection).thenReturn(connection);

            doNothing().when(connection).setAutoCommit(false);
            doNothing().when(connection).rollback();
            doNothing().when(connection).close();

            assertThrows(DataException.class, () ->
                    loanService.createLoan(
                            0,
                            new BigDecimal("100000"),
                            new BigDecimal("10"),
                            12));

            verify(connection).rollback();
            verify(connection).close();
        }
    }

    @Test
    void createLoan_daoFailure() throws Exception {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection).thenReturn(connection);

            doNothing().when(connection).setAutoCommit(false);
            doNothing().when(connection).rollback();
            doNothing().when(connection).close();

            doThrow(new RuntimeException())
                    .when(loanDao).insert(eq(connection), any(Loan.class));

            assertThrows(DataException.class, () ->
                    loanService.createLoan(
                            1L,
                            new BigDecimal("100000"),
                            new BigDecimal("10"),
                            12));

            verify(connection).rollback();
            verify(connection).close();
        }
    }

    @Test
    void getLoanById_success() throws Exception {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            Loan loan = new Loan();
            loan.setLoanId(1L);

            mocked.when(DbConnection::getConnection).thenReturn(connection);
            doNothing().when(connection).close();
            when(loanDao.getByLoanId(connection, 1L)).thenReturn(loan);

            Loan result = loanService.getLoanById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getLoanId());
            verify(connection).close();
        }
    }

    @Test
    void getLoanById_failure() {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection)
                    .thenThrow(new RuntimeException());

            assertThrows(DataException.class,
                    () -> loanService.getLoanById(1L));
        }
    }

    @Test
    void getEmiSchedule_success() throws Exception {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection).thenReturn(connection);
            doNothing().when(connection).close();
            when(emiDao.getEmiByLoanId(connection, 1L))
                    .thenReturn(List.of(new Emi()));

            List<Emi> emis = loanService.getEmiSchedule(1L);

            assertEquals(1, emis.size());
            verify(connection).close();
        }
    }

    @Test
    void getEmiSchedule_failure() {
        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection)
                    .thenThrow(new RuntimeException());

            assertThrows(DataException.class,
                    () -> loanService.getEmiSchedule(1L));
        }
    }
}
