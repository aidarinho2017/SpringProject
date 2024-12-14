package net.codejava.scheduler.config;

import net.codejava.scheduler.jobs.PromotionJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail promotionJobDetail() {
        return JobBuilder.newJob(PromotionJob.class)
                .withIdentity("promotionJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger promotionJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(30) // Выполнять каждые 30 секунд
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(promotionJobDetail())
                .withIdentity("promotionTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
