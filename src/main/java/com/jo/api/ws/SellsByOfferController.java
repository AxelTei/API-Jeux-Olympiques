package com.jo.api.ws;

import com.jo.api.pojo.SellsByOffer;
import com.jo.api.service.SellsByOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_SELLSBYOFFER)
@Tag(name = "Ventes par Offre", description = "API pour la consultation des statistiques de ventes par offre de réservation")
public class SellsByOfferController {

    @Autowired
    private SellsByOfferService sellsByOfferService;

    @Operation(summary = "Récupérer toutes les ventes par offre",
            description = "Renvoie la liste complète des statistiques de ventes par offre de réservation. Réservé aux administrateurs.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des ventes par offre récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SellsByOffer.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<SellsByOffer> getAllSellsByOffer() {
        return sellsByOfferService.getAllSellsByOffer();
    }
}
