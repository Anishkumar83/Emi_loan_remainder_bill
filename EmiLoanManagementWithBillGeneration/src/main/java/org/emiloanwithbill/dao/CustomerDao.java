package org.emiloanwithbill.dao;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.enums.Gender;
import org.emiloanwithbill.exception.DataException;
import org.emiloanwithbill.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDao.class);

    public static final int USER_ID_INDEX = 1;
    public static final int FIRSTNAME_INDEX = 2;
    public static final int LASTNAME_INDEX = 3;
    public static final int EMAIL_INDEX = 4;
    public static final int DOB_INDEX = 5;
    public static final int ADDRESS_INDEX = 6;
    public static final int GENDER_INDEX = 7;
    public static final int CUSTOMER_ID_INDEX = 7;

    public static final int GET_CUSTOMER_ID_IDX = 1;
    public static final int DELETE_CUSTOMER_ID_IDX = 1;

    String insert = """
        INSERT INTO customers ("user_id","firstName", "lastName", "email", "dob", "address", "gender")
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    String selectAll = """
        SELECT "customerId","user_id", "firstName", "lastName", "email", "dob", "address", "gender"
        FROM customers
    """;

    String getById = """
        SELECT "customerId", "firstName", "lastName", "email", "dob", "address", "gender", "user_id"
        FROM customers
        WHERE "customerId" = ?
    """;

    String update = """
        UPDATE customers SET
            "firstName" = ?,
            "lastName" = ?,
            "email" = ?,
            "dob" = ?,
            "address" = ?,
            "gender" = ?
        WHERE "customerId" = ?
    """;

    String delete = """
        DELETE FROM customers WHERE "customerId" = ?
    """;

    // ⭐ NEW METHOD — Get all customers belonging to the logged-in user
    public List<Customer> getCustomersByUserId(long userId) {
        String sql = """
            SELECT "customerId","user_id","firstName","lastName","email","dob","address","gender"
            FROM customers
            WHERE "user_id" = ?
        """;

        List<Customer> list = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getLong("customerId"));
                customer.setUser_id(rs.getLong("user_id"));
                customer.setFirstName(rs.getString("firstName"));
                customer.setLastName(rs.getString("lastName"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob").toLocalDate());
                customer.setAddress(rs.getString("address"));
                customer.setGender(Gender.valueOf(rs.getString("gender")));

                list.add(customer);
            }

            return list;

        } catch (SQLException e) {
            throw new DataException("Failed to fetch customers by userId", e);
        }
    }

    public void insert(Customer customer) {
        LOGGER.info("Inside insert function in dao");
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insert)) {

            ps.setLong(USER_ID_INDEX, customer.getUser_id());
            ps.setString(FIRSTNAME_INDEX, customer.getFirstName());
            ps.setString(LASTNAME_INDEX, customer.getLastName());
            ps.setString(EMAIL_INDEX, customer.getEmail());
            ps.setDate(DOB_INDEX, Date.valueOf(customer.getDob()));
            ps.setString(ADDRESS_INDEX, customer.getAddress());
            ps.setString(GENDER_INDEX, customer.getGender().name());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                LOGGER.error("Failed to insert customer.");
            } else {
                LOGGER.info("Customer inserted successfully.");
            }

        } catch (SQLException e) {
            throw new DataException("Insert Failed", e);
        }
    }

    public List<Customer> getAll() {
        LOGGER.info("Inside getAll function in dao");
        List<Customer> customers = new ArrayList<>();

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(selectAll)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                Customer customer = new Customer();
                customer.setCustomerId(rs.getLong("customerId"));
                customer.setUser_id(rs.getLong("user_id"));
                customer.setFirstName(rs.getString("firstName"));
                customer.setLastName(rs.getString("lastName"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob").toLocalDate());
                customer.setAddress(rs.getString("address"));
                customer.setGender(Gender.valueOf(rs.getString("gender")));

                customers.add(customer);
            }

            return customers;

        } catch (SQLException e) {
            throw new DataException("All customers retrieval failed.", e);
        }
    }

    public Customer getByCustomerId(Long customerId) {
        LOGGER.info("Inside getByCustomerId function in dao");

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(getById)) {

            ps.setLong(GET_CUSTOMER_ID_IDX, customerId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getLong("customerId"));
                customer.setUser_id(rs.getLong("user_id"));
                customer.setFirstName(rs.getString("firstName"));
                customer.setLastName(rs.getString("lastName"));
                customer.setEmail(rs.getString("email"));
                customer.setDob(rs.getDate("dob").toLocalDate());
                customer.setAddress(rs.getString("address"));
                customer.setGender(Gender.valueOf(rs.getString("gender")));
                return customer;
            }

        } catch (SQLException e) {
            throw new DataException("getByCustomerId failed", e);
        }

        return null;
    }

    public void update(Customer customer) {
        LOGGER.info("Inside update function in dao");

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(update)) {

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setDate(4, Date.valueOf(customer.getDob()));
            ps.setString(5, customer.getAddress());
            ps.setString(6, customer.getGender().name());
            ps.setLong(7, customer.getCustomerId());


            int rows = ps.executeUpdate();

            if (rows == 0) {
                LOGGER.warn("Failed to update customer.");
            } else {
                LOGGER.info("Customer updated successfully.");
            }

        } catch (SQLException e) {
            throw new DataException("Update failed: " + customer.getCustomerId(), e);
        }
    }

    public void delete(Long customerId) {
        LOGGER.info("Inside delete function in dao");

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(delete)) {

            ps.setLong(DELETE_CUSTOMER_ID_IDX, customerId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                LOGGER.info("No customer found with id={}", customerId);
            } else {
                LOGGER.info("Customer deleted successfully with id={}", customerId);
            }

        } catch (Exception e) {
            throw new DataException("Delete failed", e);
        }
    }
}
