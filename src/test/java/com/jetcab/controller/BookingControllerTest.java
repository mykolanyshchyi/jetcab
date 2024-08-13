package com.jetcab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetcab.configuration.TestConfiguration;
import com.jetcab.service.booking.BookingService;
import com.jetcab.service.booking.dto.BookingDTO;
import com.jetcab.service.booking.dto.BookingStatisticsDTO;
import com.jetcab.service.booking.dto.ModifyBookingDTO;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.notification.NotificationService;
import com.jetcab.service.taxi.TaxiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Collections;

import static com.jetcab.service.booking.model.BookingStatus.CANCELLED;
import static com.jetcab.service.booking.model.BookingStatus.COMPLETED;
import static com.jetcab.service.booking.model.BookingStatus.CONFIRMED;
import static com.jetcab.service.booking.model.BookingStatus.PENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(TestConfiguration.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private TaxiService taxiService;

    @MockBean
    private NotificationService notificationService;

    private BookingDTO bookingDTO;
    private ModifyBookingDTO modifyBookingDTO;

    @BeforeEach
    void setUp() {
        bookingDTO = BookingDTO.builder()
                .id(1L)
                .status(PENDING)
                .build();

        modifyBookingDTO = new ModifyBookingDTO();
        modifyBookingDTO.setPassengerId(1L);
        modifyBookingDTO.setPickupLocation(new ModifyLocationDTO(40.7128d, -74.0060));
        modifyBookingDTO.setDropOffLocation(new ModifyLocationDTO(40.730610, -73.935242));
    }

    @Test
    void createBooking_success() throws Exception {
        when(bookingService.createBooking(any(ModifyBookingDTO.class))).thenReturn(bookingDTO);
        when(taxiService.getAvailableTaxiIDs()).thenReturn(Collections.singletonList(1L));
        doNothing().when(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(PENDING.toString()));

        verify(bookingService).createBooking(any(ModifyBookingDTO.class));
        verify(taxiService).getAvailableTaxiIDs();
        verify(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());
    }

    @Test
    void createBooking_missingRequiredFieldPassengerId() throws Exception {
        modifyBookingDTO.setPassengerId(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void createBooking_missingRequiredFieldPickupLocation() throws Exception {
        modifyBookingDTO.setPickupLocation(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void createBooking_missingRequiredFieldLatitudeInPickupLocation() throws Exception {
        modifyBookingDTO.getPickupLocation().setLatitude(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void createBooking_missingRequiredFieldLongitudeInPickupLocation() throws Exception {
        modifyBookingDTO.getPickupLocation().setLongitude(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void createBooking_missingRequiredFieldDropOffLocation() throws Exception {
        modifyBookingDTO.setDropOffLocation(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void createBooking_missingRequiredFieldLatitudeInDropOffLocation() throws Exception {
        modifyBookingDTO.getDropOffLocation().setLatitude(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void createBooking_missingRequiredFieldLongitudeInDropOffLocation() throws Exception {
        modifyBookingDTO.getDropOffLocation().setLongitude(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_success() throws Exception {
        when(bookingService.updateBooking(anyLong(), any(ModifyBookingDTO.class))).thenReturn(bookingDTO);
        when(taxiService.getAvailableTaxiIDs()).thenReturn(Collections.singletonList(1L));
        doNothing().when(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(PENDING.toString()));

        verify(bookingService).updateBooking(anyLong(), any(ModifyBookingDTO.class));
        verify(taxiService).getAvailableTaxiIDs();
        verify(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());
    }

    @Test
    void updateBooking_missingRequiredFieldPassengerId() throws Exception {
        modifyBookingDTO.setPassengerId(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_missingRequiredFieldPickupLocation() throws Exception {
        modifyBookingDTO.setPickupLocation(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_missingRequiredFieldLatitudeInPickupLocation() throws Exception {
        modifyBookingDTO.getPickupLocation().setLatitude(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_missingRequiredFieldLongitudeInPickupLocation() throws Exception {
        modifyBookingDTO.getPickupLocation().setLongitude(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_missingRequiredFieldDropOffLocation() throws Exception {
        modifyBookingDTO.setDropOffLocation(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_missingRequiredFieldLatitudeInDropOffLocation() throws Exception {
        modifyBookingDTO.getDropOffLocation().setLatitude(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateBooking_missingRequiredFieldLongitudeInDropOffLocation() throws Exception {
        modifyBookingDTO.getDropOffLocation().setLongitude(null);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyBookingDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
        verifyNoInteractions(taxiService);
        verifyNoInteractions(notificationService);
    }

    @Test
    void cancelBooking_success() throws Exception {
        bookingDTO.setStatus(CANCELLED);
        when(bookingService.cancelBooking(anyLong())).thenReturn(bookingDTO);
        when(taxiService.getAvailableTaxiIDs()).thenReturn(Collections.singletonList(1L));
        doNothing().when(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());

        mockMvc.perform(delete("/api/v1/bookings/{bookingId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(CANCELLED.toString()));

        verify(bookingService).cancelBooking(anyLong());
        verify(taxiService).getAvailableTaxiIDs();
        verify(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());
    }

    @Test
    void takeBooking_success() throws Exception {
        bookingDTO.setStatus(CONFIRMED);
        when(bookingService.takeBooking(anyLong(), anyLong())).thenReturn(bookingDTO);
        when(taxiService.getAvailableTaxiIDs()).thenReturn(Collections.singletonList(1L));
        doNothing().when(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());

        mockMvc.perform(put("/api/v1/bookings/{bookingId}/take-booking/{taxiId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(CONFIRMED.toString()));

        verify(bookingService).takeBooking(anyLong(), anyLong());
        verify(taxiService).getAvailableTaxiIDs();
        verify(notificationService).publishBookingToAvailableTaxis(any(BookingDTO.class), any());
    }

    @Test
    void completeBooking_success() throws Exception {
        bookingDTO.setStatus(COMPLETED);
        when(bookingService.completeBooking(anyLong())).thenReturn(bookingDTO);

        mockMvc.perform(put("/api/v1/bookings/{bookingId}/complete-booking", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(COMPLETED.toString()));

        verify(bookingService).completeBooking(anyLong());
    }

    @Test
    void getBookingStatistics_success() throws Exception {
        BookingStatisticsDTO bookingStatisticsDTO = BookingStatisticsDTO.builder()
                .totalBookings(56)
                .inProgressBookings(7)
                .completedBookings(47)
                .cancelledBookings(2)
                .build();

        when(bookingService.getBookingStatistics(any(ZonedDateTime.class), any(ZonedDateTime.class)))
                .thenReturn(bookingStatisticsDTO);

        mockMvc.perform(get("/api/v1/bookings/statistics")
                        .param("from", ZonedDateTime.now().minusDays(4).toString())
                        .param("to", ZonedDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBookings").value(56))
                .andExpect(jsonPath("$.inProgressBookings").value(7))
                .andExpect(jsonPath("$.completedBookings").value(47))
                .andExpect(jsonPath("$.cancelledBookings").value(2));
    }

    private String serializeToString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}