package com.jo.api.service;

import com.jo.api.pojo.Booking;
import com.jo.api.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking createBooking(Booking booking) {
        bookingRepository.save(booking);
        return booking;
    }

    public void deleteBookingById(Long id) {
        bookingRepository.deleteById(id);
    }
}
