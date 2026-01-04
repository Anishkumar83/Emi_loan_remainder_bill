package org.emiloanwithbill.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.emiloanwithbill.exception.DataException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DbConnection {

    private static final HikariDataSource dataSource;
    private static final int MAX_POOL_SIZE=10;
    private static final int MIN_IDLE =5;

    private DbConnection() {}

    static {
        try {
            InputStream input = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                throw new DataException("db.properties not found in classpath");
            }

            Properties prop = new Properties();

            prop.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(prop.getProperty("db.url"));
            config.setUsername(prop.getProperty("db.username"));
            config.setPassword(prop.getProperty("db.password"));

            config.setDriverClassName("org.postgresql.Driver");

            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);

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
