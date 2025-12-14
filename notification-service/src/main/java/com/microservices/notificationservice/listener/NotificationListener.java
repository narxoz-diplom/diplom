package com.microservices.notificationservice.listener;

import com.microservices.notificationservice.config.RabbitMQConfig;
import com.microservices.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(Map<String, Object> message) {
        log.info("Received notification message: {}", message);
        notificationService.processNotificationMessage(message);
    }
}



