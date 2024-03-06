package com.catchpoint.tracing.examples.todo.config;

import com.catchpoint.tracing.examples.todo.service.TodoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author myildiz
 */
@Configuration
@ConditionalOnProperty(value = "spring.schedule.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class Scheduler {

    private static final long FIVE_MINUTES = 5 * 60 * 1000;

    private final TodoService service;
    
    public Scheduler(TodoService service) {
        this.service = service;
    }
    
    @Scheduled(fixedRate = FIVE_MINUTES, initialDelay = FIVE_MINUTES)
    private void clearOldTodos() {
        service.clearOldTodos();
    }

}
