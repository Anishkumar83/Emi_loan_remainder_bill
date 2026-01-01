package org.emiloanwithbill.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.emiloanwithbill.exception.DataException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private static final HikariDataSource dataSource;

    private DbConnection() {}

    static {
        try {
            Properties prop = new Properties();
            InputStream input = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                throw new DataException("db.properties not found in classpath");
            }

            prop.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(prop.getProperty("db.url"));
            config.setUsername(prop.getProperty("db.username"));
            config.setPassword(prop.getProperty("db.password"));

            config.setDriverClassName("org.postgresql.Driver");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            throw new DataException("Failed to initialize HikariCP", e);
        }
    }


    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DataException("Failed to get DB connection", e);
        }
    }
}
