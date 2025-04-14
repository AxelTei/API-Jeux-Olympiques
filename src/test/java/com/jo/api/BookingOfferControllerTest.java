package com.jo.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.jo.api.pojo.BookingOffer;
import com.jo.api.service.BookingOfferService;
import com.jo.api.ws.BookingOfferController;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BookingOfferControllerTest {

    @Mock
    private BookingOfferService bookingOfferService;

    @InjectMocks
    private BookingOfferController bookingOfferController;


    @Test
    void testGetBookById_Success() throws Exception {

        // construction de ma données
        BookingOffer mockBookingOffer = new BookingOffer();
        mockBookingOffer.setBookingOfferId(1L);
        mockBookingOffer.setTitle("Duo");
        mockBookingOffer.setPrice(BigDecimal.valueOf(10.00));
        mockBookingOffer.setNumberOfCustomers(2);

        when(bookingOfferService.getBookingOfferById(mockBookingOffer.getBookingOfferId())).thenReturn(mockBookingOffer);

        ResponseEntity<BookingOffer> response = bookingOfferController.getBookingOfferById(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BookingOffer responseBookingOffer = response.getBody();
        if(responseBookingOffer != null && responseBookingOffer.getTitle() != null) {
            assertThat(responseBookingOffer.getTitle()).isEqualTo("Duo");
        } else {
            fail();
        }
    }

    @Test
    void testGetBookingOfferById_NotFound() {
        ResponseEntity<BookingOffer> response = bookingOfferController.getBookingOfferById(1000L);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void testDeleteBook_Success() throws Exception {
        Long bookingOfferId = 1L;
        ResponseEntity<Void> response = bookingOfferController.deleteBookingOfferById(bookingOfferId);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void testCreateBookingOffer_Success() throws Exception {
        BookingOffer newBookingOffer = new BookingOffer();
        newBookingOffer.setTitle("Trio");
        newBookingOffer.setPrice(BigDecimal.valueOf(15.00));
        newBookingOffer.setNumberOfCustomers(3);

        when(bookingOfferService.createBookingOffer(newBookingOffer)).thenReturn(newBookingOffer);

        BookingOffer response = bookingOfferController.createBookingOffer(newBookingOffer);

        assertThat(response.getTitle()).isEqualTo("Trio");
        assertThat(response.getNumberOfCustomers()).isEqualTo(3);
    }

    @Test
    void testCreateBookingOffer_Failure_TitleMissing() throws Exception {
        BookingOffer newBookingOffer = new BookingOffer();
        newBookingOffer.setPrice(BigDecimal.valueOf(15.00));
        newBookingOffer.setNumberOfCustomers(3);

        when(bookingOfferService.createBookingOffer(newBookingOffer)).thenThrow(new IllegalArgumentException("Title cannot be null"));

        try {
            bookingOfferController.createBookingOffer(newBookingOffer);
        } catch(Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Title cannot be null");
        }
    }

    @Test
    void testUpdateBook_Success() {
        Long bookingOfferId = 1L;
        BookingOffer updatedBookingOffer = new BookingOffer();
        updatedBookingOffer.setBookingOfferId(bookingOfferId);
        updatedBookingOffer.setTitle("Family");
        updatedBookingOffer.setPrice(BigDecimal.valueOf(30.00));
        updatedBookingOffer.setNumberOfCustomers(4);

        when(bookingOfferService.updateBookingOfferById(bookingOfferId, updatedBookingOffer)).thenReturn(updatedBookingOffer);

        ResponseEntity<BookingOffer> response = bookingOfferController.updateBookingOfferById(bookingOfferId, updatedBookingOffer);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        if(response.getBody() != null) {
            assertThat(response.getBody().getTitle()).isEqualTo("Family");
            assertThat(response.getBody().getNumberOfCustomers()).isEqualTo(4);
        } else {
            fail();
        }
    }

    @Test
    void testUpdateBook_NotFound() {
        Long bookingOfferId = 999L;
        BookingOffer updatedBookingOffer = new BookingOffer();
        updatedBookingOffer.setBookingOfferId(bookingOfferId);
        updatedBookingOffer.setTitle("Family");
        updatedBookingOffer.setPrice(BigDecimal.valueOf(30.00));
        updatedBookingOffer.setNumberOfCustomers(4);

        when(bookingOfferService.updateBookingOfferById(bookingOfferId, updatedBookingOffer)).thenReturn(null);

        ResponseEntity<BookingOffer> response = bookingOfferController.updateBookingOfferById(bookingOfferId, updatedBookingOffer);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
