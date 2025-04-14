package com.jo.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.jo.api.pojo.BookingOffer;
import com.jo.api.service.BookingOfferService;
import com.jo.api.ws.BookingOfferController;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingOfferControllerTest {

    @Mock
    private BookingOfferService bookingOfferService;

    @InjectMocks
    private BookingOfferController bookingOfferController;


    @Test
    void testGetBookById_Success() throws Exception {

        // construction de ma données
        BookingOffer mockBookinOffer = new BookingOffer();
        mockBookinOffer.setBookingOfferId(1L);
        mockBookinOffer.setTitle("Duo");
        mockBookinOffer.setPrice(BigDecimal.valueOf(10.00));
        mockBookinOffer.setNumberOfCustomers(2);

        when(bookingOfferService.getBookingOfferById(mockBookinOffer.getBookingOfferId())).thenReturn(mockBookinOffer);

        ResponseEntity<BookingOffer> response = bookingOfferController.getBookingOfferById(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

}
