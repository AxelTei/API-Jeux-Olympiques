package com.jo.api.ws;

import com.jo.api.pojo.Booking;
import com.jo.api.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_BOOKING)
@Tag(name = "Réservations", description = "API pour la gestion des réservations")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Récupérer toutes les réservations",
            description = "Renvoie la liste complète des réservations dans le système")
    @ApiResponse(responseCode = "200",
            description = "Liste des réservations récupérée avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Booking.class)))
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @Operation(summary = "Récupérer une réservation par ID",
            description = "Renvoie une réservation spécifique identifiée par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation trouvée",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(
            @Parameter(description = "ID de la réservation à récupérer", required = true)
            @PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return (booking != null) ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Créer une nouvelle réservation",
            description = "Crée une nouvelle réservation dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Données de réservation invalides",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")))
    })
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Parameter(description = "Détails de la réservation à créer", required = true)
            @RequestBody Booking booking) {
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur: " + e.getMessage());
        }
    }

    @Operation(summary = "Supprimer une réservation",
            description = "Supprime une réservation existante identifiée par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingById(
            @Parameter(description = "ID de la réservation à supprimer", required = true)
            @PathVariable Long id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }
}
