package _1danhebojo.coalarm.coalarm_service.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);      // 동시 처리 쓰레드 수
        executor.setMaxPoolSize(20);       // 최대 쓰레드 수
        executor.setQueueCapacity(100);    // 대기 큐 수용량
        executor.setThreadNamePrefix("SSE-Async-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "kimchiTaskExecutor")
    public Executor kimchiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Kimchi-Async-");
        executor.initialize();
        return executor;
    }
}