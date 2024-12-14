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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PromotionJob extends QuartzJobBean {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        // Получение данных из JobDataMap
        @SuppressWarnings("unchecked")
        List<String> recipientEmails = (List<String>) jobDataMap.get("emails");
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");

        if (recipientEmails == null || recipientEmails.isEmpty()) {
            System.err.println("No recipient emails provided. Skipping email sending.");
            return;
        }

        // Отправка письма каждому адресату
        for (String recipientEmail : recipientEmails) {
            sendMail(mailProperties.getUsername(), recipientEmail, subject, body);
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
            System.out.println("Email sent to: " + toEmail);
        } catch (MessagingException ex) {
            System.err.println("Failed to send email to " + toEmail + ": " + ex.getMessage());
        }
    }
}
