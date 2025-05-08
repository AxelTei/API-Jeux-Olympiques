package com.jo.api.service;

import com.jo.api.dto.CreatePaymentDto;
import com.jo.api.dto.CreatePaymentResponseDto;
import com.jo.api.dto.PaymentIntentDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StripeService {

    // Map pour stocker les PaymentIntents simulés
    private final Map<String, PaymentIntentDto> paymentIntents = new HashMap<>();

    public CreatePaymentResponseDto createPaymentIntent (CreatePaymentDto createPaymentDto) {
        // Génère un ID unique pour simuler un ID Stripe
        String paymentIntentId = "pi_" + UUID.randomUUID().toString().replace("-", "");

        // Génère un client_secret pour simuler celui de Stripe
        String clientSecret = paymentIntentId + "_secret_" + UUID.randomUUID().toString().replace("-", "");

        // Crée un payment intent simulé
        PaymentIntentDto paymentIntent = new PaymentIntentDto(
                paymentIntentId,
                createPaymentDto.getAmount(),
                "eur", //devise
                "requires_payment_method", // statut initial
                clientSecret,
                System.currentTimeMillis()
        );

        // Stocke le payment intent
        paymentIntents.put(paymentIntentId, paymentIntent);

        return new CreatePaymentResponseDto(clientSecret, null);
    }

    public PaymentIntentDto getPaymentIntent(String id) {
        PaymentIntentDto paymentIntent = paymentIntents.get(id);

        if (paymentIntent == null) {
            throw new RuntimeException("Payment intent not found");
        }

        return paymentIntent;
    }

    // Méthode pour simuler la confirmation d'un paiement (possibilité de l'appeler sur un autre endpoint)
    public PaymentIntentDto confirmPaymentIntent(String id) {
        PaymentIntentDto paymentIntent = paymentIntents.get(id);

        if (paymentIntent == null) {
            throw new RuntimeException("Payment intent not found");
        }

        // Mise à jour du statut
        PaymentIntentDto updatedIntent = new PaymentIntentDto(
                paymentIntent.getId(),
                paymentIntent.getAmount(),
                paymentIntent.getCurrency(),
                "succeeded", // changement de statut
                paymentIntent.getClientSecret(),
                paymentIntent.getCreatedAt()
        );

        // Mise à jour dans la map
        paymentIntents.put(id, updatedIntent);

        return updatedIntent;
    }
}
