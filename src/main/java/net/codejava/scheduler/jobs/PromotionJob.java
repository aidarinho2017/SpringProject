package net.codejava.scheduler.jobs;

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

@Component
public class PromotionJob extends QuartzJobBean {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String subject = "Big Sale Alert!";
        String body = "We have amazing discounts on laptops! Don't miss out.";
        String recipientEmail = "a_issakhanov@kbtu.kz"; // Замените на нужный адрес или создайте логику для получения списка адресов.

        sendMail(mailProperties.getUsername(), recipientEmail, subject, body);
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
            System.err.println("Failed to send email: " + ex.getMessage());
        }
    }
}
