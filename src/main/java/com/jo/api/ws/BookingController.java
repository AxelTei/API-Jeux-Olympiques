package com.jo.api.ws;

import com.jo.api.pojo.Booking;
import com.jo.api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_BOOKING)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * retourne toutes les réservations
     * @return a list of bookings
     */
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    /**
     *
     * retourne une seule réservation
     * @return one booking
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return (booking != null) ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingOfferById(@PathVariable Long id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }
}
