package com.jo.api.ws;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.service.BookingOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER)
public class BookingOfferController {

    @Autowired
    private BookingOfferService bookingOfferService;

    /**
     * retourne toutes les offres de réservation
     * @return une liste d'offre de réservation
     */
    @GetMapping
    public List<BookingOffer> getAllBookingOffers() {
        return bookingOfferService.getAllBookingOffers();
    }

    /**
     *
     * retourne une seule offre de réservation
     * @return une offre de réservation
     */
    @GetMapping("/{title}")
    public BookingOffer getBookingOfferByTitle(@PathVariable("title") String title) {
        return bookingOfferService.getBookingOfferByTitle(title);
    }
}
