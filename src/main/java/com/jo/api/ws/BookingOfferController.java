package com.jo.api.ws;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.service.BookingOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public BookingOffer createBookingOffer(@RequestBody BookingOffer bookingOffer) {
        return bookingOfferService.createBookingOffer(bookingOffer);
    }

    @DeleteMapping("/{id}")
    public void deleteBookingOfferById(PathVariable Long id) {
        return bookingOfferService.deleteBookingOfferById(id);
    }
}
