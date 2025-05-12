package com.jo.api.service;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.pojo.SellsByOffer;
import com.jo.api.repository.BookingOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingOfferService {

    @Autowired
    private BookingOfferRepository bookingOfferRepository;

    public List<BookingOffer> getAllBookingOffers() {
        return bookingOfferRepository.findAll();
    }

    public BookingOffer getBookingOfferById(Long id) {
        return bookingOfferRepository.findById(id).orElse(null);
    }

    public BookingOffer createBookingOffer(BookingOffer bookingOffer) {
        SellsByOffer sellsByOffer = new SellsByOffer();
        sellsByOffer.setOfferTitle(bookingOffer.getTitle());
        sellsByOffer.setOfferPrice(bookingOffer.getPrice());
        sellsByOffer.setSells(0);
        bookingOffer.setSellsByOffer(sellsByOffer);
        sellsByOffer.setBookingOffer(bookingOffer);
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

            if (oldBookingOffer.getSellsByOffer() != null) {
                oldBookingOffer.getSellsByOffer().setOfferTitle(bookingOffer.getTitle());
                oldBookingOffer.getSellsByOffer().setOfferPrice(bookingOffer.getPrice());
            }
            this.bookingOfferRepository.save(oldBookingOffer);
        }
        return oldBookingOffer;
    }

    public BookingOffer getBookingOfferByTitle(String title) {
        return bookingOfferRepository.findByTitle(title).orElse(null);
    }
}
