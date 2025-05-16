package com.jo.api;

import com.jo.api.pojo.Ticket;
import com.jo.api.request.TicketRequest;
import com.jo.api.response.TicketResponse;
import com.jo.api.service.BookingOfferService;
import com.jo.api.service.TicketService;
import com.jo.api.ws.TicketController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private BookingOfferService bookingOfferService;

    @InjectMocks
    private TicketController ticketController;

    @Test
    void testGetAllTickets_Success() {
        // Préparation des données de test
        List<Ticket> mockTickets = new ArrayList<>();
        Ticket ticket1 = createMockTicket(1L, "user1", "Duo", "Concert", BigDecimal.valueOf(20.00), 2, "Ticket non utilisé");
        Ticket ticket2 = createMockTicket(2L, "user2", "Family", "Festival", BigDecimal.valueOf(40.00), 4, "Ticket utilisé");
        mockTickets.add(ticket1);
        mockTickets.add(ticket2);

        when(ticketService.getAllTickets()).thenReturn(mockTickets);

        // Exécution du test
        ResponseEntity<List<Ticket>> response = ticketController.getAllTickets();

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Ticket> tickets = response.getBody();
        assertThat(tickets).hasSize(2);
        assertThat(tickets.get(0).getTicketId()).isEqualTo(1L);
        assertThat(tickets.get(1).getTicketId()).isEqualTo(2L);
    }

    @Test
    void testGetAllTickets_Error() {
        // Préparation des données de test
        when(ticketService.getAllTickets()).thenThrow(new RuntimeException("Database error"));

        // Exécution du test
        ResponseEntity<List<Ticket>> response = ticketController.getAllTickets();

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testGetTicketByQrCodeKey_Success() {
        // Préparation des données de test
        String qrCodeKey = "ABC123";
        Ticket mockTicket = createMockTicket(1L, "user1", "Duo", "Concert", BigDecimal.valueOf(20.00), 2, "Ticket non utilisé");
        mockTicket.setQrCodeKey(qrCodeKey);

        when(ticketService.getTicketByQrCodeKey(qrCodeKey)).thenReturn(mockTicket);

        // Exécution du test
        ResponseEntity<?> response = ticketController.getTicketByQrCodeKey(qrCodeKey);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(TicketResponse.class);

        TicketResponse ticketResponse = (TicketResponse) response.getBody();
        assertThat(ticketResponse.getTicketId()).isEqualTo(1L);
        assertThat(ticketResponse.getUsername()).isEqualTo("user1");
        assertThat(ticketResponse.getOfferTitle()).isEqualTo("Duo");
        assertThat(ticketResponse.getNumberOfGuests()).isEqualTo(String.valueOf(2));
    }

    @Test
    void testGetTicketByQrCodeKey_NotFound() {
        // Préparation des données de test
        String qrCodeKey = "INVALID";
        when(ticketService.getTicketByQrCodeKey(qrCodeKey)).thenThrow(new NoSuchElementException("Ticket not found"));

        // Exécution du test
        ResponseEntity<?> response = ticketController.getTicketByQrCodeKey(qrCodeKey);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Ticket non trouvé avec la clé QR: " + qrCodeKey);
    }

    @Test
    void testGetTicketByQrCodeKey_Error() {
        // Préparation des données de test
        String qrCodeKey = "ABC123";
        when(ticketService.getTicketByQrCodeKey(qrCodeKey)).thenThrow(new RuntimeException("Database error"));

        // Exécution du test
        ResponseEntity<?> response = ticketController.getTicketByQrCodeKey(qrCodeKey);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Une erreur est survenue lors de la récupération du ticket");
    }

    @Test
    void testVerifyTicket_Success() {
        // Préparation des données de test
        String qrCodeKey = "ABC123";
        Ticket mockTicket = createMockTicket(1L, "user1", "Duo", "Concert", BigDecimal.valueOf(20.00), 2, "Ticket utilisé");
        mockTicket.setQrCodeKey(qrCodeKey);

        doNothing().when(ticketService).validateTicket(qrCodeKey);
        when(ticketService.getTicketByQrCodeKey(qrCodeKey)).thenReturn(mockTicket);

        // Exécution du test
        ResponseEntity<?> response = ticketController.verifyTicket(qrCodeKey);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(TicketResponse.class);

        TicketResponse ticketResponse = (TicketResponse) response.getBody();
        assertThat(ticketResponse.getTicketId()).isEqualTo(1L);
        assertThat(ticketResponse.getUsed()).isEqualTo("Ticket utilisé");
    }

    @Test
    void testVerifyTicket_NotFound() {
        // Préparation des données de test
        String qrCodeKey = "INVALID";
        doThrow(new NoSuchElementException("Ticket not found")).when(ticketService).validateTicket(qrCodeKey);

        // Exécution du test
        ResponseEntity<?> response = ticketController.verifyTicket(qrCodeKey);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Ticket non trouvé avec la clé QR: " + qrCodeKey);
    }

    @Test
    void testVerifyTicket_AlreadyUsed() {
        // Préparation des données de test
        String qrCodeKey = "ABC123";
        doThrow(new IllegalStateException("Ticket déjà utilisé")).when(ticketService).validateTicket(qrCodeKey);

        // Exécution du test
        ResponseEntity<?> response = ticketController.verifyTicket(qrCodeKey);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Ticket déjà utilisé");
    }

    @Test
    void testCreateTicket_Success() {
        // Préparation des données de test
        TicketRequest request = new TicketRequest();
        request.setOfferTitle("Duo");
        request.setUsername("user1");
        request.setNumberOfGuests(String.valueOf(2));
        request.setTicketPrice(BigDecimal.valueOf(20.00));

        Ticket createdTicket = createMockTicket(1L, "user1", "Duo", "Concert", BigDecimal.valueOf(20.00), 2, "Ticket non utilisé");

        doNothing().when(bookingOfferService).incrementSellsForOffer("Duo");
        when(ticketService.createTicket(request)).thenReturn(createdTicket);

        // Exécution du test
        ResponseEntity<?> response = ticketController.createTicket(request);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(Ticket.class);

        Ticket ticketResponse = (Ticket) response.getBody();
        assertThat(ticketResponse.getTicketId()).isEqualTo(1L);
        assertThat(ticketResponse.getUsername()).isEqualTo("user1");
        assertThat(ticketResponse.getOfferTitle()).isEqualTo("Duo");
    }

    @Test
    void testCreateTicket_InvalidRequest() {
        // Préparation des données de test
        TicketRequest request = new TicketRequest();
        // Omettre le titre de l'offre pour provoquer une erreur de validation
        request.setUsername("user1");

        // Exécution du test
        ResponseEntity<?> response = ticketController.createTicket(request);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Le titre de l'offre est requis");
    }

    @Test
    void testCreateTicket_OfferNotFound() {
        // Préparation des données de test
        TicketRequest request = new TicketRequest();
        request.setOfferTitle("InvalidOffer");
        request.setUsername("user1");
        request.setNumberOfGuests(String.valueOf(2));
        request.setTicketPrice(BigDecimal.valueOf(20.00));

        doThrow(new NoSuchElementException("Offer not found")).when(bookingOfferService).incrementSellsForOffer("InvalidOffer");

        // Exécution du test
        ResponseEntity<?> response = ticketController.createTicket(request);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Offre non trouvée: InvalidOffer");
    }

    @Test
    void testCreateTicket_Error() {
        // Préparation des données de test
        TicketRequest request = new TicketRequest();
        request.setOfferTitle("Duo");
        request.setUsername("user1");
        request.setNumberOfGuests(String.valueOf(2));
        request.setTicketPrice(BigDecimal.valueOf(20.00));

        doNothing().when(bookingOfferService).incrementSellsForOffer("Duo");
        when(ticketService.createTicket(request)).thenThrow(new RuntimeException("Database error"));

        // Exécution du test
        ResponseEntity<?> response = ticketController.createTicket(request);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Une erreur est survenue lors de la création du ticket");
    }

    // Méthode utilitaire pour créer des tickets de test
    private Ticket createMockTicket(Long id, String username, String offerTitle, String eventName,
                                    BigDecimal price, int guests, String used) {
        Ticket ticket = new Ticket();
        ticket.setTicketId(id);
        ticket.setUsername(username);
        ticket.setOfferTitle(offerTitle);
        ticket.setEventName(eventName);
        ticket.setTicketPrice(price);
        ticket.setNumberOfGuests(String.valueOf(guests));
        ticket.setPurchaseDate(String.valueOf(LocalDateTime.now()));
        ticket.setUsed(used);
        return ticket;
    }
}

