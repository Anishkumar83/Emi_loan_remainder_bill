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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomerDaoTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;


    @BeforeEach
    void setUp() throws Exception{
        mockConnection=mock(Connection.class);
        mockPreparedStatement=mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1);
    }

    @Test
    void testInsertCustomer() throws Exception{
        try(MockedStatic<DbConnection> mockDb= mockStatic(DbConnection.class)){
            mockDb.when(DbConnection::getConnection)
                    .thenReturn(mockConnection);

            CustomerDao customerDao= new CustomerDao();

            assertDoesNotThrow(()->customerDao.insert(createCustomer()),
                    "Insert Customer Failed");

            verify(mockPreparedStatement,times(1)).executeUpdate();
            verify(mockConnection,times(1)).prepareStatement(anyString());

        }
    }

    @Test
    void testInsertCustomerFail() throws Exception{
        when(mockPreparedStatement.executeUpdate())
                .thenThrow(new SQLException("Db failed."));

        try(MockedStatic<DbConnection> mockDb= mockStatic(DbConnection.class)){
            mockDb.when(DbConnection::getConnection).thenReturn(mockConnection);

            CustomerDao customerDao= new CustomerDao();

            Customer customer = createCustomer();
            assertThrows(DataException.class,()->customerDao.insert(customer),
                    "Customer insert failed");

            verify(mockPreparedStatement,times(1)).executeUpdate();
            verify(mockConnection,times(1)).prepareStatement(anyString());
        }

    }

    private Customer createCustomer(){
        Customer customer = new Customer();
        customer.setFirstName("Anish");
        customer.setLastName("Kumar");
        customer.setEmail("ak@gmail.com");
        customer.setDob(LocalDate.of(2001,7,16));
        customer.setAddress("Chennai");
        customer.setGender(Gender.MALE);
        return customer;
    }

}
