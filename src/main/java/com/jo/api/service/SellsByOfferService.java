package com.jo.api.service;

import com.jo.api.pojo.SellsByOffer;
import com.jo.api.repository.SellsByOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellsByOfferService {

    @Autowired
    private SellsByOfferRepository sellsByOfferRepository;

    public List<SellsByOffer> getAllSellsByOffer() {
        return sellsByOfferRepository.findAll();
    }
}
