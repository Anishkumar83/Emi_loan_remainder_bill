package org.emiloanwithbill.service.serviceimplementation;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.dao.EmiDao;
import org.emiloanwithbill.service.EmiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class EmiServiceImplementation implements EmiService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmiServiceImplementation.class);

    private final EmiDao emiDao = new EmiDao();

    @Override
    public void updateStatus(long emiId, String status) {

        LOGGER.info("Updating EMI = {} to status = {}", emiId, status);

        try (Connection con = DbConnection.getConnection()) {

            if (con == null) {
                LOGGER.error("DB CONNECTION IS NULL!");
                throw new RuntimeException("Database connection failed");
            }

            emiDao.updateStatus(con, emiId, status);

        } catch (Exception e) {
            LOGGER.error("Failed to update EMI", e);
            throw new RuntimeException("Failed to update EMI status", e);
        }
    }
}
