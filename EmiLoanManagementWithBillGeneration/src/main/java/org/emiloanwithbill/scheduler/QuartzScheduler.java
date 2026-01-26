package org.emiloanwithbill.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzScheduler {

    private static Scheduler scheduler;

    public static void startScheduler() throws Exception {

        scheduler = StdSchedulerFactory.getDefaultScheduler();

        // Job detail
        JobDetail jobDetail = JobBuilder.newJob(EmiReminderJob.class)
                .withIdentity("emiReminderJob", "emiGroup")
                .build();

        // Trigger -> Every day at 9:00 AM
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("emiReminderTrigger", "emiGroup")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0 9 * * ?")
                )
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();

        System.out.println("Quartz Scheduler Started (Daily @ 9 AM)");
    }

    public static void stopScheduler() throws Exception {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}