package com.jo.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

}
