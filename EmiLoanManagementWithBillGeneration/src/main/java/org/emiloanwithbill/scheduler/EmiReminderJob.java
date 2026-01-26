package org.emiloanwithbill.scheduler;

import org.emiloanwithbill.config.DbConnection;
import org.emiloanwithbill.dao.*;
import org.emiloanwithbill.dto.BillCustomerDto;
import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.util.PdfCreation;
import org.emiloanwithbill.util.SendEmail;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmiReminderJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        BillReminderDao billDao = new BillReminderDao();
        EmailAuditDao auditDao = new EmailAuditDao();
        EmiDao emiDao = new EmiDao();

        List<BillCustomerDto> dueList = billDao.fetchCustomersForTomorrow(tomorrow);

        for (BillCustomerDto dto : dueList) {
            try {

                // Fetch EMI Record
                Emi emi = emiDao.getEmiById(DbConnection.getConnection(), dto.getEmiId());

                // Password = DOB (ddMMyyyy)
                String password = dto.getDob().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

                // Generate Updated PDF (center QR + link)
                PdfCreation pdf = new PdfCreation();
                String pdfPath = pdf.generateEmiBillPdf(dto.getName(), password);

                // Load HTML Email Template
                InputStream is = getClass().getClassLoader()
                        .getResourceAsStream("templates/Email-content.html");

                SendEmail email = new SendEmail();

                String html = email.loadHtml(is)
                        .replace("{{USERNAME}}", dto.getName())
                        .replace("{{YEAR}}", String.valueOf(LocalDate.now().getYear()))
                        .replace("{{EMI_AMOUNT}}", String.valueOf(emi.getEmiAmount()))
                        .replace("{{DUE_DATE}}", emi.getDueDate().toString())
                        .replace("{{OUTSTANDING_BALANCE}}", String.valueOf(emi.getOutstandingBalance()));

                // Email Subject
                String subject = "EMI Due Reminder - " + emi.getDueDate();

                // Send Email with PDF Attachment
                email.sendEmailWithAttachment(dto.getEmail(), subject, pdfPath, html);

                // Save Audit Log – SUCCESS
                auditDao.saveAudit(
                        dto.getEmiId(),
                        dto.getCustomerId(),
                        dto.getEmail(),
                        subject,
                        "SUCCESS",
                        null
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                // Save Audit Log – FAILED
                auditDao.saveAudit(
                        dto.getEmiId(),
                        dto.getCustomerId(),
                        dto.getEmail(),
                        "EMI Reminder",
                        "FAILED",
                        ex.getMessage()
                );
            }
        }
    }
}
