package org.emiloanwithbill.listener;


import org.emiloanwithbill.scheduler.QuartzScheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            QuartzScheduler.startScheduler();
            System.out.println("Quartz started on application startup.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            QuartzScheduler.stopScheduler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
