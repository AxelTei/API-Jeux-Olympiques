package com.jo.api.service;

import com.jo.api.pojo.BookingOffer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingOfferService {

    private final List<BookingOffer> bookingOffers = new ArrayList<>();

    private final AtomicLong counter = new AtomicLong(1);

    public List<BookingOffer> getAllBookingOffers() {
        return bookingOffers;
    }

    public BookingOffer getBookingOfferByTitle(String title) {
        return bookingOffers.stream()
                .filter(bookingOffer -> bookingOffer.getTitle().equals(title))
                .findAny()
                .orElse(null);
    }

    public BookingOffer createBookingOffer(BookingOffer bookingOffer) {
        bookingOffer.setBookingOfferId(counter.getAndIncrement());
        bookingOffers.add(bookingOffer);
        return bookingOffer;
    }

    public void deleteBookingOfferById(Long id) {
        bookingOffers.removeIf(bookingOffer -> bookingOffer.getBookingOfferId().equals(id));
    }
}
