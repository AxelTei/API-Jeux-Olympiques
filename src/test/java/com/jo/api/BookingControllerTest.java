package com.jo.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.jo.api.pojo.Booking;
import com.jo.api.service.BookingService;
import com.jo.api.ws.BookingController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void testGetAllBookings_Success() {
        // Préparation des données de test
        List<Booking> mockBookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setBookingId(1L);
        Booking booking2 = new Booking();
        booking2.setBookingId(2L);
        mockBookings.add(booking1);
        mockBookings.add(booking2);

        when(bookingService.getAllBookings()).thenReturn(mockBookings);

        // Exécution du test
        List<Booking> response = bookingController.getAllBookings();

        // Vérification des résultats
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getBookingId()).isEqualTo(1L);
        assertThat(response.get(1).getBookingId()).isEqualTo(2L);
    }

    @Test
    void testGetBookingById_Success() {
        // Préparation des données de test
        Booking mockBooking = new Booking();
        mockBooking.setBookingId(1L);

        when(bookingService.getBookingById(1L)).thenReturn(mockBooking);

        // Exécution du test
        ResponseEntity<Booking> response = bookingController.getBookingById(1L);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Booking responseBooking = response.getBody();
        if (responseBooking != null) {
            assertThat(responseBooking.getBookingId()).isEqualTo(1L);
        } else {
            fail("Response body should not be null");
        }
    }

    @Test
    void testGetBookingById_NotFound() {
        // Mock du service retournant null pour simuler une réservation non trouvée
        when(bookingService.getBookingById(999L)).thenReturn(null);

        // Exécution du test
        ResponseEntity<Booking> response = bookingController.getBookingById(999L);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateBooking_Success() {
        // Préparation des données de test
        Booking newBooking = new Booking();
        newBooking.setBookingId(1L);

        when(bookingService.createBooking(newBooking)).thenReturn(newBooking);

        // Exécution du test
        ResponseEntity<?> response = bookingController.createBooking(newBooking);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Booking createdBooking = (Booking) response.getBody();
        if (createdBooking != null) {
            assertThat(createdBooking.getBookingId()).isEqualTo(1L);
        } else {
            fail("Response body should not be null");
        }
    }

    @Test
    void testCreateBooking_Failure() {
        // Préparation des données de test
        Booking invalidBooking = new Booking();

        when(bookingService.createBooking(invalidBooking)).thenThrow(new IllegalArgumentException("Données invalides"));

        // Exécution du test
        ResponseEntity<?> response = bookingController.createBooking(invalidBooking);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Erreur: Données invalides");
    }

    @Test
    void testDeleteBooking_Success() {
        // Exécution du test
        ResponseEntity<Void> response = bookingController.deleteBookingById(1L);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Vérification que la méthode du service a été appelée
        verify(bookingService, times(1)).deleteBookingById(1L);
    }
}
