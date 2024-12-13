package net.codejava.scheduler.controller;

import lombok.extern.slf4j.Slf4j;
import net.codejava.scheduler.jobs.PromotionJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/schedule")
public class AdminSchedulerController {

    @Autowired
    private Scheduler scheduler;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/promotion")
    public ResponseEntity<String> schedulePromotionEmails() {
        try {
            // Список email-адресов
            List<String> emails = Arrays.asList("a_issakhanov@kbtu.kz", "a_seidazym@kbtu.kz");

            // Тема и текст письма
            String subject = "Big Sale Alert!";
            String body = "We have amazing discounts on laptops! Don't miss out.";

            // Создаём задачу и триггер
            JobDetail jobDetail = buildJobDetail(emails, subject, body);
            Trigger trigger = buildTrigger(jobDetail);

            scheduler.scheduleJob(jobDetail, trigger);

            return ResponseEntity.ok("Promotion emails scheduled to repeat every 10 seconds!");
        } catch (SchedulerException e) {
            log.error("Error scheduling promotion emails", e);
            return ResponseEntity.status(500).body("Error scheduling promotion emails.");
        }
    }

    private JobDetail buildJobDetail(List<String> emails, String subject, String body) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("emails", emails);
        jobDataMap.put("subject", subject);
        jobDataMap.put("body", body);

        return JobBuilder.newJob(PromotionJob.class)
                .withIdentity(UUID.randomUUID().toString(), "promotion-jobs")
                .withDescription("Send Promotion Emails")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "promotion-triggers")
                .withDescription("Send Promotion Emails Trigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10) // Интервал 10 секунд
                        .repeatForever() // Повторять бесконечно
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }
}
