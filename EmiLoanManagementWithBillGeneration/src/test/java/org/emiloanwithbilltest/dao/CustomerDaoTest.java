package org.emiloanwithbilltest.dao;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.enums.Gender;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomerDaoTest {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() throws Exception {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        customerDao = new CustomerDao();

        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
    }

    @Test
    void insert_success() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertDoesNotThrow(() -> customerDao.insert(createCustomer()));

            verify(preparedStatement).executeUpdate();
            verify(connection).close();
        }
    }

    @Test
    void insert_failure() throws Exception {
        when(preparedStatement.executeUpdate())
                .thenThrow(new SQLException("DB error"));

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertThrows(DataException.class,
                    () -> customerDao.insert(createCustomer()));
        }
    }

    @Test
    void getAll_success() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        when(resultSet.getLong("customerId")).thenReturn(1L);
        when(resultSet.getString("firstName")).thenReturn("Anish");
        when(resultSet.getString("lastName")).thenReturn("Kumar");
        when(resultSet.getString("email")).thenReturn("ak@gmail.com");
        when(resultSet.getDate("dob")).thenReturn(Date.valueOf("2001-07-16"));
        when(resultSet.getString("address")).thenReturn("Chennai");
        when(resultSet.getString("gender")).thenReturn("MALE");

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            List<Customer> customers = customerDao.getAll();

            assertEquals(1, customers.size());
            assertEquals("Anish", customers.get(0).getFirstName());
        }
    }

    @Test
    void getAll_failure() throws Exception {
        when(preparedStatement.executeQuery())
                .thenThrow(new SQLException("DB error"));

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertThrows(DataException.class, () -> customerDao.getAll());
        }
    }

    @Test
    void getByCustomerId_success() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        when(resultSet.getLong("customerId")).thenReturn(1L);
        when(resultSet.getString("firstName")).thenReturn("Anish");
        when(resultSet.getString("lastName")).thenReturn("Kumar");
        when(resultSet.getString("email")).thenReturn("ak@gmail.com");
        when(resultSet.getDate("dob")).thenReturn(Date.valueOf("2001-07-16"));
        when(resultSet.getString("address")).thenReturn("Chennai");
        when(resultSet.getString("gender")).thenReturn("MALE");

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            Customer customer = customerDao.getByCustomerId(1L);

            assertNotNull(customer);
            assertEquals(1L, customer.getCustomerId());
        }
    }

    @Test
    void getByCustomerId_notFound() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            Customer customer = customerDao.getByCustomerId(1L);

            assertNull(customer);
        }
    }

    @Test
    void update_success() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertDoesNotThrow(() -> customerDao.update(createCustomerWithId()));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void update_failure() throws Exception {
        when(preparedStatement.executeUpdate())
                .thenThrow(new SQLException("DB error"));

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertThrows(DataException.class,
                    () -> customerDao.update(createCustomerWithId()));
        }
    }

    @Test
    void delete_success() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertDoesNotThrow(() -> customerDao.delete(1L));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void delete_failure() throws Exception {
        when(preparedStatement.executeUpdate())
                .thenThrow(new SQLException("DB error"));

        try (MockedStatic<DbConnection> mocked = mockStatic(DbConnection.class)) {
            mocked.when(DbConnection::getConnection).thenReturn(connection);

            assertThrows(DataException.class,
                    () -> customerDao.delete(1L));
        }
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Anish");
        customer.setLastName("Kumar");
        customer.setEmail("ak@gmail.com");
        customer.setDob(LocalDate.of(2001, 7, 16));
        customer.setAddress("Chennai");
        customer.setGender(Gender.MALE);
        return customer;
    }

    private Customer createCustomerWithId() {
        Customer customer = createCustomer();
        customer.setCustomerId(1L);
        return customer;
    }
}
