package com.jetcab.service.notification;

import com.jetcab.service.booking.dto.BookingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.EnableRetry;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@EnableRetry
@SpringBootTest
class NotificationServiceImplTest {

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationServiceImpl notificationService;

    private ArgumentCaptor<String> destinationCaptor;

    @BeforeEach
    void setUp() {
        destinationCaptor = ArgumentCaptor.forClass(String.class);
        notificationService = Mockito.spy(notificationService);
    }

    @Test
    void publishBookingToAvailableTaxis_NoAvailableTaxis() {
        notificationService.publishBookingToAvailableTaxis(new BookingDTO(), emptyList());
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void publishBookingToAvailableTaxis_sendNotificationToCorrectDestination() {
        List<String> expectedDestinations = List.of("/topic/bookings/34", "/topic/bookings/78");
        List<Long> availableTaxiIDs = List.of(34L, 78L);

        notificationService.publishBookingToAvailableTaxis(new BookingDTO(), availableTaxiIDs);

        verify(messagingTemplate, times(2)).convertAndSend(destinationCaptor.capture(), any(BookingDTO.class));
        List<String> actualDestinations = destinationCaptor.getAllValues();
        assertNotNull(actualDestinations);
        assertEquals(2, actualDestinations.size());
        actualDestinations.sort(String::compareTo);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    void publishBookingToAvailableTaxis_sendNotificationToCorrectDestinationWithRetry() {
        List<Long> availableTaxiIDs = List.of(34L, 78L);

        doThrow(new MessagingException("network issue"))
                .doNothing()
                .when(messagingTemplate).convertAndSend(eq("/topic/bookings/78"), any(BookingDTO.class));

        notificationService.publishBookingToAvailableTaxis(new BookingDTO(), availableTaxiIDs);

        verify(messagingTemplate).convertAndSend(eq("/topic/bookings/34"), any(BookingDTO.class));
        verify(messagingTemplate, times(2)).convertAndSend(eq("/topic/bookings/78"), any(BookingDTO.class));
    }
}