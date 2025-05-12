package com.jo.api.ws;

import com.jo.api.pojo.Ticket;
import com.jo.api.request.TicketRequest;
import com.jo.api.response.TicketResponse;
import com.jo.api.service.BookingOfferService;
import com.jo.api.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( value = ApiRegistration.API + ApiRegistration.REST_TICKET)
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BookingOfferService bookingOfferService;

    /**
     * retourne tous les tickets
     * @return a list of tickets
     */
    @GetMapping
    public List<Ticket> getAllBookings() {
        return ticketService.getAllTickets();
    }

    // Récupérer les détails d'un ticket grâce à la clef du Qr code
    @GetMapping("/{qrCodeKey}")
    public ResponseEntity<TicketResponse> getTicketByQrCodeKey(@PathVariable String qrCodeKey) {
        Ticket ticket = ticketService.getTicketByQrCodeKey(qrCodeKey);
        TicketResponse response = convertToResponse(ticket);
        return ResponseEntity.ok(response);
    }

    // Vérifier les détails d'un ticket
    @GetMapping("/verify-ticket/{qrCodeKey}")
    public ResponseEntity<TicketResponse> verifyTicket(@PathVariable String qrCodeKey) {
        ticketService.validateTicket(qrCodeKey);
        Ticket ticket = ticketService.getTicketByQrCodeKey(qrCodeKey);
        TicketResponse response = convertToResponse(ticket);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Ticket createTicket(@RequestBody TicketRequest request) {
        bookingOfferService.incrementSellsForOffer(request.getOfferTitle());
        return ticketService.createTicket(request);
    }

    private TicketResponse convertToResponse(Ticket ticket) {
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
