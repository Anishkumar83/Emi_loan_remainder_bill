package org.emiloanwithbilltest.dao;

import org.emiloanwithbill.dao.EmiDao;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Emi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmiDaoTest {

    @InjectMocks
    private EmiDao emiDao;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private Emi emi;

    @BeforeEach
    void setUp() {
        emi = new Emi(
                1L,
                10L,
                5000.0,
                500.0,
                4500.0,
                20000.0,
                LocalDate.now(),
                "PENDING"
        );
    }

    @Test
    void saveEmi_success() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate())
                .thenReturn(1);

        assertDoesNotThrow(() ->
                emiDao.saveEmi(connection, emi));

        verify(preparedStatement).executeUpdate();
    }

    @Test
    void saveEmi_failure_shouldThrowException() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate())
                .thenReturn(0);

        assertThrows(DataException.class, () ->
                emiDao.saveEmi(connection, emi));
    }

    @Test
    void getEmiByLoanId_success() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
                .thenReturn(resultSet);

        when(resultSet.next())
                .thenReturn(true, false);

        when(resultSet.getLong("emiId")).thenReturn(1L);
        when(resultSet.getLong("loanId")).thenReturn(10L);
        when(resultSet.getDouble("emi_amount")).thenReturn(5000.0);
        when(resultSet.getDouble("interest_component")).thenReturn(500.0);
        when(resultSet.getDouble("principal_component")).thenReturn(4500.0);
        when(resultSet.getDouble("outstanding_balance")).thenReturn(20000.0);
        when(resultSet.getDate("due_date"))
                .thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("status")).thenReturn("PENDING");

        List<Emi> result = emiDao.getEmiByLoanId(connection, 10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getLoanId());
    }

    @Test
    void getEmiByLoanId_sqlException_shouldThrowDataException()
            throws Exception {

        when(connection.prepareStatement(anyString()))
                .thenThrow(SQLException.class);

        assertThrows(DataException.class, () ->
                emiDao.getEmiByLoanId(connection, 10L));
    }

    @Test
    void updateStatus_success() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate())
                .thenReturn(1);

        assertDoesNotThrow(() ->
                emiDao.updateStatus(connection, 1L, "PAID"));
    }

    @Test
    void updateStatus_sqlException_shouldThrowDataException()
            throws Exception {

        when(connection.prepareStatement(anyString()))
                .thenThrow(SQLException.class);

        assertThrows(DataException.class, () ->
                emiDao.updateStatus(connection, 1L, "PAID"));
    }
}
