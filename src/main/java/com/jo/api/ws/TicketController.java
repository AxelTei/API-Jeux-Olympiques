package com.jo.api.ws;

import com.jo.api.pojo.Ticket;
import com.jo.api.request.TicketRequest;
import com.jo.api.response.TicketResponse;
import com.jo.api.service.BookingOfferService;
import com.jo.api.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = ApiRegistration.API + ApiRegistration.REST_TICKET)
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BookingOfferService bookingOfferService;

    /**
     * Retourne tous les tickets
     * @return a list of tickets
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        try {
            List<Ticket> tickets = ticketService.getAllTickets();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            // Log l'erreur (idéalement avec un logger comme SLF4J)
            System.err.println("Erreur lors de la récupération des tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les détails d'un ticket grâce à la clef du QR code
     */
    @GetMapping("/{qrCodeKey}")
    public ResponseEntity<?> getTicketByQrCodeKey(@PathVariable String qrCodeKey) {
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

    /**
     * Vérifie les détails d'un ticket
     */
    @GetMapping("/verify-ticket/{qrCodeKey}")
    public ResponseEntity<?> verifyTicket(@PathVariable String qrCodeKey) {
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

    /**
     * Crée un nouveau ticket
     */
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody TicketRequest request) {
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