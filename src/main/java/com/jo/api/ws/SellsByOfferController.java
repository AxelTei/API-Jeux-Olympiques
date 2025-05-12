package com.jo.api.ws;

import com.jo.api.pojo.SellsByOffer;
import com.jo.api.service.SellsByOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_SELLSBYOFFER)
public class SellsByOfferController {

    @Autowired
    private SellsByOfferService sellsByOfferService;

    /**
     * retourne toutes les ventes par offre de réservation
     * @return a list of sells by offer
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<SellsByOffer> getAllSellsByOffer() {
        return sellsByOfferService.getAllSellsByOffer();
    }
}
