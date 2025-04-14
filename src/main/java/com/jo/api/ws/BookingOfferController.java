package com.jo.api.ws;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.service.BookingOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER)
public class BookingOfferController {

    @Autowired
    private BookingOfferService bookingOfferService;

    /**
     * retourne toutes les offres de réservation
     * @return a list of booking offers
     */
    @GetMapping
    public List<BookingOffer> getAllBookingOffers() {
        return bookingOfferService.getAllBookingOffers();
    }

    /**
     *
     * retourne une seule offre de réservation
     * @return one booking offer
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingOffer> getBookingOfferById(@PathVariable Long id) {
        //return bookingOfferService.getBookingOfferById(id);
        BookingOffer bookingOffer = bookingOfferService.getBookingOfferById(id);
        return (bookingOffer != null) ? ResponseEntity.ok(bookingOffer) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public BookingOffer createBookingOffer(@RequestBody BookingOffer bookingOffer) {
        return bookingOfferService.createBookingOffer(bookingOffer);
    }

    @DeleteMapping("/{id}")
    public void deleteBookingOfferById(@PathVariable Long id) {
        bookingOfferService.deleteBookingOfferById(id);
    }

    @PutMapping("/{id}")
    public BookingOffer updateBookingOfferById(@PathVariable Long id, @RequestBody BookingOffer bookingOffer) {
        return bookingOfferService.updateBookingOfferById(id, bookingOffer);
    }
}
