package com.jo.api.service;

import com.jo.api.pojo.Ticket;
import com.jo.api.repository.TicketRepository;
import com.jo.api.request.TicketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket createTicket(TicketRequest request) {
        Ticket ticket = new Ticket();

        ticket.setUsername(request.getUsername());
        ticket.setOfferTitle(request.getOfferTitle());
        ticket.setEventName("Jeux Olympiques de Paris 2024");
        ticket.setTicketPrice(request.getTicketPrice());
        ticket.setNumberOfGuests(request.getNumberOfGuests());

        //Create new paymentKey

        UUID randomUUID = UUID.randomUUID();
        String paymentKey = randomUUID.toString().replaceAll("-", "");

        ticket.setPaymentKey(paymentKey);
        ticket.setQrCodeKey(request.getUserKey() + paymentKey);

        // Date actuelle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ticket.setPurchaseDate(LocalDateTime.now().format(formatter));

        //Ticket non utilisé par défaut
        ticket.setUsed("Ticket non utilisé");

        return ticketRepository.save(ticket);
    }

    public Ticket getTicketByQrCodeKey(String qrCodeKey) {
        return ticketRepository.findByQrCodeKey(qrCodeKey)
                .orElseThrow(() -> new RuntimeException("La clé du Qr code n'a retrouvé aucun ticket"));
    }

    public void validateTicket(String qrCodeKey) {
        Ticket ticket = getTicketByQrCodeKey(qrCodeKey);
        // Marquer le ticker comme utilisé
        ticket.setUsed("Ticket utilisé");
        ticketRepository.save(ticket);
    }
}
