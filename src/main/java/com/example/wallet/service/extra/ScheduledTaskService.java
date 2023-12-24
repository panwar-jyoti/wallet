package com.example.wallet.service.extra;

import com.example.wallet.model.extra.ScheduledTaskResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

//@Service
//public class ScheduledTaskService {
//
//    private final ScheduledTaskResultRepository scheduledTaskResultRepository;
//
//    @Autowired
//    public ScheduledTaskService(ScheduledTaskResultRepository scheduledTaskResultRepository) {
//        this.scheduledTaskResultRepository = scheduledTaskResultRepository;
//    }
//
//    @Scheduled(fixedRate = 60000) // Run every 60 seconds
//    public void performScheduledTask() {
//        // Implement your scheduled task logic here
//        System.out.println("Scheduled task executed at: " + LocalDateTime.now());
//
//        // Example: Save the result to the database
//        ScheduledTaskResult taskResult = new ScheduledTaskResult();
//        taskResult.setExecutionTime(LocalDateTime.now());
//        taskResult.setTaskName("YourScheduledTaskName"); // Customize the task name
//        scheduledTaskResultRepository.save(taskResult);
//    }
//}
