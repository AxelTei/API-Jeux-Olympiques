package com.jo.api.repository;

import com.jo.api.pojo.SellsByOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellsByOfferRepository extends JpaRepository<SellsByOffer, Long> {
}
