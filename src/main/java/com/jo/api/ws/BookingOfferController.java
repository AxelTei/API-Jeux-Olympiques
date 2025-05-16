package com.jo.api.ws;

import com.jo.api.pojo.BookingOffer;
import com.jo.api.service.BookingOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_BOOKINGOFFER)
@Tag(name = "Offres de Réservation", description = "API pour la gestion des offres de réservation")
public class BookingOfferController {

    @Autowired
    private BookingOfferService bookingOfferService;

    @Operation(summary = "Récupérer toutes les offres de réservation",
        description = "Renvoie la liste complète des offres de réservation disponibles")
    @ApiResponse(responseCode = "200",
        description = "Liste des offres récupérée avec succès",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = BookingOffer.class)))
    @GetMapping
    public List<BookingOffer> getAllBookingOffers() {
        return bookingOfferService.getAllBookingOffers();
    }

    @Operation(summary = "Récupérer une offre de réservation par ID",
        description = "Renvoie une offre de réservation spécifique identifiée par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offre trouvée",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingOffer.class))),
            @ApiResponse(responseCode = "404", description = "Offre non trouvée",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingOffer> getBookingOfferById(
            @Parameter(description = "ID de l'offre à récupérer", required = true)
            @PathVariable Long id) {
        BookingOffer bookingOffer = bookingOfferService.getBookingOfferById(id);
        return (bookingOffer != null) ? ResponseEntity.ok(bookingOffer) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Récupérer une offre de réservation par titre",
            description = "Renvoie une offre de réservation spécifique identifiée par son titre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offre trouvée",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookingOffer.class))),
            @ApiResponse(responseCode = "404", description = "Offre non trouvée",
                    content = @Content)
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<BookingOffer> getBookingOfferByTitle(
            @Parameter(description = "Titre de l'offre à récupérer", required = true)
            @PathVariable String title) {
        BookingOffer bookingOffer = bookingOfferService.getBookingOfferByTitle(title);
        return (bookingOffer != null) ? ResponseEntity.ok(bookingOffer) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Créer une nouvelle offre de réservation",
            description = "Crée une nouvelle offre de réservation dans le système",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offre créée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookingOffer.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public BookingOffer createBookingOffer(
            @Parameter(description = "Détails de l'offre à créer", required = true)
            @RequestBody BookingOffer bookingOffer) {
        return bookingOfferService.createBookingOffer(bookingOffer);
    }

    @Operation(summary = "Supprimer une offre de réservation",
            description = "Supprime une offre de réservation existante identifiée par son ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Offre supprimée avec succès",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Offre non trouvée",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingOfferById(
            @Parameter(description = "ID de l'offre à supprimer", required = true)
            @PathVariable Long id) {
        bookingOfferService.deleteBookingOfferById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mettre à jour une offre de réservation",
            description = "Met à jour une offre de réservation existante identifiée par son ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offre mise à jour avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookingOffer.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Offre non trouvée",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookingOffer> updateBookingOfferById(
            @Parameter(description = "ID de l'offre à mettre à jour", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'offre", required = true)
            @RequestBody BookingOffer bookingOffer) {
        BookingOffer updatedBookingOffer = bookingOfferService.updateBookingOfferById(id, bookingOffer);
        return (updatedBookingOffer != null) ? ResponseEntity.ok(updatedBookingOffer) : ResponseEntity.notFound().build();
    }
}
