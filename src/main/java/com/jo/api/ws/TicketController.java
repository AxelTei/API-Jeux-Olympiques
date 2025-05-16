package com.jo.api.ws;

import com.jo.api.pojo.Ticket;
import com.jo.api.request.TicketRequest;
import com.jo.api.response.TicketResponse;
import com.jo.api.service.BookingOfferService;
import com.jo.api.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = ApiRegistration.API + ApiRegistration.REST_TICKET)
@Tag(name = "Tickets", description = "API pour la gestion des tickets d'événements")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BookingOfferService bookingOfferService;

    @Operation(summary = "Récupérer tous les tickets",
            description = "Renvoie la liste complète des tickets dans le système",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des tickets récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Ticket.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        try {
            List<Ticket> tickets = ticketService.getAllTickets();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Récupérer un ticket par clé QR",
            description = "Renvoie les détails d'un ticket spécifique identifié par sa clé QR")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket non trouvé",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")))
    })
    @GetMapping("/{qrCodeKey}")
    public ResponseEntity<?> getTicketByQrCodeKey(
            @Parameter(description = "Clé QR du ticket à récupérer", required = true)
            @PathVariable String qrCodeKey) {
        try {
            Ticket ticket = ticketService.getTicketByQrCodeKey(qrCodeKey);
            TicketResponse response = convertToResponse(ticket);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // Ticket non trouvé
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Ticket non trouvé avec la clé QR: " + qrCodeKey);
        } catch (Exception e) {
            // Autre erreur
            System.err.println("Erreur lors de la récupération du ticket: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la récupération du ticket");
        }
    }

    @Operation(summary = "Vérifier et valider un ticket",
            description = "Vérifie la validité d'un ticket et le marque comme utilisé si valide",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket vérifié et validé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ticket déjà utilisé ou invalide",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Ticket non trouvé",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")))
    })
    @GetMapping("/verify-ticket/{qrCodeKey}")
    public ResponseEntity<?> verifyTicket(
            @Parameter(description = "Clé QR du ticket à vérifier", required = true)
            @PathVariable String qrCodeKey) {
        try {
            ticketService.validateTicket(qrCodeKey);
            Ticket ticket = ticketService.getTicketByQrCodeKey(qrCodeKey);
            TicketResponse response = convertToResponse(ticket);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // Ticket non trouvé
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Ticket non trouvé avec la clé QR: " + qrCodeKey);
        } catch (IllegalStateException e) {
            // Ticket déjà utilisé ou autre problème de validation
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            // Autre erreur
            System.err.println("Erreur lors de la vérification du ticket: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la vérification du ticket");
        }
    }

    @Operation(summary = "Créer un nouveau ticket",
            description = "Crée un nouveau ticket pour une offre spécifiée",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket créé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Ticket.class))),
            @ApiResponse(responseCode = "400", description = "Données de ticket invalides",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Offre spécifiée non trouvée",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")))
    })
    @PostMapping
    public ResponseEntity<?> createTicket(
            @Parameter(description = "Informations du ticket à créer", required = true)
            @RequestBody TicketRequest request) {
        try {
            // Valider les données de la requête
            if (request.getOfferTitle() == null || request.getOfferTitle().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Le titre de l'offre est requis");
            }

            bookingOfferService.incrementSellsForOffer(request.getOfferTitle());
            Ticket createdTicket = ticketService.createTicket(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (NoSuchElementException e) {
            // Offre non trouvée
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Offre non trouvée: " + request.getOfferTitle());
        } catch (Exception e) {
            // Autre erreur
            System.err.println("Erreur lors de la création du ticket: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création du ticket");
        }
    }

    private TicketResponse convertToResponse(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketResponse response = new TicketResponse();
        response.setTicketId(ticket.getTicketId());
        response.setUsername(ticket.getUsername());
        response.setOfferTitle(ticket.getOfferTitle());
        response.setEventName(ticket.getEventName());
        response.setTicketPrice(ticket.getTicketPrice());
        response.setNumberOfGuests(ticket.getNumberOfGuests());
        response.setPurchaseDate(ticket.getPurchaseDate());
        response.setUsed(ticket.getUsed());
        return response;
    }
}