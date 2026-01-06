package org.emiloanwithbilltest.dao;

import org.emiloanwithbill.dao.LoanDao;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.emiloanwithbill.config.DbConnection;

@ExtendWith(MockitoExtension.class)
class LoanDaoTest {

    @InjectMocks
    private LoanDao loanDao;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private Loan loan;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        loan.setLoanId(1L);
        loan.setCustomerId(10L);
        loan.setPrincipal(500000);
        loan.setInterestRate(8.5);
        loan.setTenureMonths(240);
    }

    @Test
    void insert_success() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(1L);

        assertDoesNotThrow(() -> loanDao.insert(connection, loan));
        assertEquals(1L, loan.getLoanId());
    }

    @Test
    void insert_noRowsAffected_shouldThrowException() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DataException.class,
                () -> loanDao.insert(connection, loan));
    }

    @Test
    void insert_generatedKeyMissing_shouldThrowException() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertThrows(DataException.class,
                () -> loanDao.insert(connection, loan));
    }

    @Test
    void getByLoanId_success() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        when(resultSet.getLong("loanId")).thenReturn(1L);
        when(resultSet.getLong("customerId")).thenReturn(10L);
        when(resultSet.getDouble("principal")).thenReturn(500000.0);
        when(resultSet.getDouble("interestRate")).thenReturn(8.5);
        when(resultSet.getInt("tenureMonths")).thenReturn(240);

        Loan result = loanDao.getByLoanId(connection, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getLoanId());
    }

    @Test
    void getByLoanId_notFound_shouldReturnNull() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
                .thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Loan result = loanDao.getByLoanId(connection, 99L);
        assertNull(result);
    }

    @Test
    void getByLoanId_sqlException_shouldThrowDataException()
            throws Exception {

        when(connection.prepareStatement(anyString()))
                .thenThrow(SQLException.class);

        assertThrows(DataException.class,
                () -> loanDao.getByLoanId(connection, 1L));
    }

    @Test
    void getAll_success() throws Exception {
        try (MockedStatic<DbConnection> mocked =
                     mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeQuery())
                    .thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true, false);

            when(resultSet.getLong("loanId")).thenReturn(1L);
            when(resultSet.getLong("customerId")).thenReturn(10L);
            when(resultSet.getDouble("principal")).thenReturn(500000.0);
            when(resultSet.getDouble("interestRate")).thenReturn(8.5);
            when(resultSet.getInt("tenureMonths")).thenReturn(240);

            List<Loan> loans = loanDao.getAll();
            assertEquals(1, loans.size());
        }
    }

    @Test
    void update_success() throws Exception {
        try (MockedStatic<DbConnection> mocked =
                     mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate())
                    .thenReturn(1);

            assertDoesNotThrow(() -> loanDao.update(loan));
        }
    }


    @Test
    void delete_success() throws Exception {
        try (MockedStatic<DbConnection> mocked =
                     mockStatic(DbConnection.class)) {

            mocked.when(DbConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate())
                    .thenReturn(1);

            assertDoesNotThrow(() -> loanDao.delete(1L));
        }
    }

}
