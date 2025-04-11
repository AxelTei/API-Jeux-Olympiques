package com.jo.api.service;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.repository.BookingOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingOfferService {

    @Autowired
    private BookingOfferRepository bookingOfferRepository;

    public List<BookingOffer> getAllBookingOffers() {
        return bookingOfferRepository.findAll();
    }

    public BookingOffer getBookingOfferByTitle(String title) {
        return bookingOfferRepository.findByTitle(title);
    }

    public BookingOffer getBookingOfferById(Long id) {
        return bookingOfferRepository.findById(id).stream()
                .filter(bookingOffer -> bookingOffer.getBookingOfferId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public BookingOffer createBookingOffer(BookingOffer bookingOffer) {
        bookingOfferRepository.save(bookingOffer);
        return bookingOffer;
    }

    public void deleteBookingOfferById(Long id) {
        bookingOfferRepository.deleteById(id);
    }

    public BookingOffer updateBookingOfferById(Long id, BookingOffer bookingOffer) {
        BookingOffer oldBookingOffer = this.getBookingOfferById(id);
        if (oldBookingOffer != null) {
            oldBookingOffer.setTitle(bookingOffer.getTitle());
            oldBookingOffer.setPrice(bookingOffer.getPrice());
            oldBookingOffer.setNumberOfCustomers(bookingOffer.getNumberOfCustomers());
            this.bookingOfferRepository.save(oldBookingOffer);
        }
        return oldBookingOffer;
    }
}
