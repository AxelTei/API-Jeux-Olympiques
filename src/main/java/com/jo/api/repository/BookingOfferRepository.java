package com.jo.api.repository;

import com.jo.api.pojo.BookingOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingOfferRepository extends JpaRepository<BookingOffer, Long> {
    Optional<BookingOffer> findByTitle(String title);
}
