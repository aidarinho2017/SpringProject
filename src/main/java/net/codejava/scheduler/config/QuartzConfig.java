package net.codejava.scheduler.config;

import net.codejava.scheduler.jobs.SampleJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // Определение задачи
    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob(SampleJob.class)
                .withIdentity("sampleJob") // Уникальный идентификатор задачи
                .storeDurably() // Хранить задачу даже без активного триггера
                .build();
    }

    // Создание триггера для задачи
    @Bean
    public Trigger sampleJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(30) // Интервал выполнения задачи
                .repeatForever(); // Бесконечное повторение

        return TriggerBuilder.newTrigger()
                .forJob(sampleJobDetail()) // Связываем с задачей
                .withIdentity("sampleTrigger") // Уникальный идентификатор триггера
                .withSchedule(scheduleBuilder) // Привязываем расписание
                .build();
    }
}