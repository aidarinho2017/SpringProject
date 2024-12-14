package net.codejava.scheduler.jobs;


import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EmailJob extends QuartzJobBean {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        @SuppressWarnings("unchecked")
        List<String> recipientEmails = (List<String>) jobDataMap.get("emails");

        if (recipientEmails == null || recipientEmails.isEmpty()) {
//            log.warn("No recipient emails provided. Skipping job execution.");
            return; // Пропускаем выполнение, если список email пуст
        }

        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");

        for (String recipientEmail : recipientEmails) {
            try {
                sendMail(mailProperties.getUsername(), recipientEmail, subject, body);
//                log.info("Email sent to {}", recipientEmail);
            } catch (Exception e) {
//                log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage());
            }
        }
    }

    private void sendMail(String fromEmail, String toEmail, String subject, String body) {
        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            mailSender.send(message);
        } catch (MessagingException ex) {
            System.out.println(ex);
        }
    }
}
