package ru.riji.sql_to_influx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ComponentScan(
        basePackages="ru.riji.sql_to_influx")
public class ThreadPoolTaskSchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
