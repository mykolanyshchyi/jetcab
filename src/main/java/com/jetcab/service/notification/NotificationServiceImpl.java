package com.jetcab.service.notification;

import com.jetcab.service.booking.dto.BookingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final String TAXI_ENDPOINT_TEMPLATE = "/topic/bookings/%s";

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Autowired
    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate, @Lazy NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public void publishBookingToAvailableTaxis(BookingDTO booking, List<Long> availableTaxisIDs) {
        availableTaxisIDs.parallelStream().forEach(taxiId -> notificationService.notifyTaxi(booking, taxiId));
    }

    @Retryable(
            retryFor = {MessagingException.class},
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public void notifyTaxi(BookingDTO booking, Long taxiId) {
        String taxiEndpoint = String.format(TAXI_ENDPOINT_TEMPLATE, taxiId);
        try {
            log.info(taxiEndpoint);
            messagingTemplate.convertAndSend(taxiEndpoint, booking);
        } catch (MessagingException e) {
            log.warn("Failed to send booking notification to taxi with ID {}. Retrying...", taxiId);
            throw e;
        }
    }

    @Recover
    public void recover(MessagingException e, BookingDTO booking, Long taxiId) {
        log.error("Failed to send booking notification to taxi with ID {} after retries", taxiId, e);
        // todo: implement fallback logic
    }
}
