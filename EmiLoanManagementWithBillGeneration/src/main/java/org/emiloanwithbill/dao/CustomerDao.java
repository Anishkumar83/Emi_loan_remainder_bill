package org.emiloanwithbill.dao;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.enums.Gender;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CustomerDao {

    static final Logger LOGGER = LoggerFactory.getLogger(CustomerDao.class);
    public static final int FIRSTNAME_INDEX = 1;
    public static final int LASTNAME_INDEX = 2;
    public static final int EMAIL_INDEX = 3;
    public static final int DOB_INDEX = 4;
    public static final int ADDRESS_INDEX = 5;
    public static final int GENDER_INDEX = 6;

    public static final int CUSTOMER_ID_INDEX = 7;

    String insert = """
            INSERT INTO customers (firstName, lastName, email, dob, address, gender)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    String selectAll = """
            SELECT * FROM customers
            """;

    String getById = """
            SELECT * FROM customers
            WHERE customerId = ?
            """;

    String update = """
            UPDATE customers set
            firstName = ?,
            lastName = ?,
            email = ?,
            dob = ?,
            address = ?
            WHERE customerId = ?
            """;

    String delete = """
            DELETE FROM customers WHERE id = ?
            """;

    public void insert(Customer customer) {
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insert)) {

            ps.setString(FIRSTNAME_INDEX, customer.getFirstName());
            ps.setString(LASTNAME_INDEX, customer.getLastName());
            ps.setString(EMAIL_INDEX, customer.getEmail());
            ps.setDate(DOB_INDEX, customer.getDob());
            ps.setString(ADDRESS_INDEX, customer.getAddress());
            ps.setString(GENDER_INDEX, customer.getGender().name());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.error("Failed to insert customer.");
            } else {
                LOGGER.info("Customer inserted successfully.");
            }
        } catch (SQLException e) {
            throw new DataException("Insert Failed", e);
        }
    }

    public List<Customer> getAll(){
        List<Customer> customers = new ArrayList<>();
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(selectAll)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getLong("customerId"));
                customer.setFirstName(rs.getString("firstName"));
                customer.setLastName(rs.getString("lastName"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob"));
                customer.setAddress(rs.getString("address"));
                customer.setGender(Gender.valueOf(rs.getString("gender")));

                customers.add(customer);
            }
            LOGGER.info("All customers retrieved successfully.");

        } catch (SQLException e) {
            LOGGER.error("All customers retrieval failed.", e);
            throw new DataException("All customers retrieved failed.", e);
        }
        return customers;
    }

    public Customer getByCustomerId(Long customerId){
        try(Connection con=DbConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(getById)) {
            ps.setLong(1,customerId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getLong("customerId"));
                customer.setFirstName(rs.getString("firstName"));
                customer.setLastName(rs.getString("lastName"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob"));
                customer.setAddress(rs.getString("address"));
                customer.setGender(Gender.valueOf(rs.getString("gender")));
                return customer;
            }

        }catch(SQLException e){
            LOGGER.error("getByCustomerId failed.",e);
        }
        return null;
    }

    public void update(Customer customer) {
        try(Connection con=DbConnection.getConnection();
        PreparedStatement ps= con.prepareStatement(update)){
            ps.setString(FIRSTNAME_INDEX, customer.getFirstName());
            ps.setString(LASTNAME_INDEX, customer.getLastName());
            ps.setString(EMAIL_INDEX, customer.getEmail());
            ps.setDate(DOB_INDEX, customer.getDob());
            ps.setString(ADDRESS_INDEX, customer.getAddress());
            ps.setString(GENDER_INDEX, customer.getGender().name());
            ps.setLong(CUSTOMER_ID_INDEX,customer.getCustomerId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                LOGGER.warn("Failed to update customer.");
            }else  {
                LOGGER.info("Customer updated successfully.");
            }
        }catch(SQLException e){
            LOGGER.error("update failed.",e);
            throw new DataException("Update failed.", e);
        }
    }
}
