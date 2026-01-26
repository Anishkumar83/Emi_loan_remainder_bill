package org.emiloanwithbilltest;

import org.emiloanwithbill.util.PdfCreation;
import org.emiloanwithbill.util.SendEmail;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmiReminderMockTest {

   @Test
   public void testMockEmiReminder() {
      try {
         System.out.println("\n===== START MOCK EMI TEST (NO DB) =====");

         long mockEmiId = 301;
         String customerName = "Mock User";
         String customerEmail = "anishkumarbe2023@gmail.com";
         LocalDate dob = LocalDate.of(2001, 7, 16);

         System.out.println("✔ Customer: " + customerName);
         System.out.println("✔ Email: " + customerEmail);

         String password = dob.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

         PdfCreation pdf = new PdfCreation();
         String pdfPath = pdf.generateEmiBillPdf(customerName, password);

         System.out.println("✔ PDF created: " + pdfPath);
         System.out.println("✔ PDF password (DOB): " + password);

         SendEmail mail = new SendEmail();

         InputStream is = getClass().getClassLoader()
                 .getResourceAsStream("templates/Email-content.html");

         if (is == null) {
            System.out.println("✘ Template NOT FOUND!");
            return;
         }

         String html = mail.loadHtml(is)
                 .replace("{{USERNAME}}", customerName)
                 .replace("{{YEAR}}", String.valueOf(LocalDate.now().getYear()));

         System.out.println("✔ Template Loaded");


         mail.sendEmailWithAttachment(
                 customerEmail,
                 "Mock EMI Reminder Notification",
                 pdfPath,
                 html
         );

         System.out.println("===== EMAIL SENT SUCCESSFULLY =====");
         System.out.println("===== END OF MOCK TEST =====");

      } catch (Exception e) {
         System.out.println("✘ MOCK TEST FAILED");
         e.printStackTrace();
      }
   }
}
