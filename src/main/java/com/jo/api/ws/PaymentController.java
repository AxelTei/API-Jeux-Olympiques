package com.jo.api.ws;

import com.jo.api.dto.ConfirmPaymentDto;
import com.jo.api.dto.CreatePaymentDto;
import com.jo.api.dto.CreatePaymentResponseDto;
import com.jo.api.dto.PaymentIntentDto;
import com.jo.api.service.StripeService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ApiRegistration.API + ApiRegistration.REST_PAYMENT)
@Tag(name = "Paiements", description = "API pour la gestion des paiements via Stripe")
public class PaymentController {

    private final StripeService stripeService;

    @Autowired
    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @Operation(summary = "Créer une intention de paiement",
            description = "Crée une nouvelle intention de paiement via Stripe",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intention de paiement créée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreatePaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la création de l'intention de paiement",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreatePaymentResponseDto.class)))
    })
    @PostMapping("/create-payment-intent")
    public ResponseEntity<CreatePaymentResponseDto> createPaymentIntent (
            @Parameter(description = "Détails de l'intention de paiement à créer", required = true)
            @RequestBody CreatePaymentDto createPaymentDto) {
        try {
            CreatePaymentResponseDto response = stripeService.createPaymentIntent(createPaymentDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CreatePaymentResponseDto(null, e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer une intention de paiement",
            description = "Récupère les détails d'une intention de paiement existante via son ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intention de paiement récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentIntentDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la récupération de l'intention de paiement",
                    content = @Content)
    })
    @GetMapping("/payment-intent/{id}")
    public ResponseEntity<PaymentIntentDto> getPaymentIntent (
            @Parameter(description = "ID de l'intention de paiement à récupérer", required = true)
            @PathVariable String id) {
        try {
            PaymentIntentDto paymentIntent = stripeService.getPaymentIntent(id);
            return ResponseEntity.ok(paymentIntent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Confirmer un paiement (simulation)",
            description = "Confirme une intention de paiement sans passer par Stripe (simulation uniquement)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement confirmé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentIntentDto.class))),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la confirmation du paiement",
                    content = @Content)
    })
    @PostMapping("/confirm-payment")
    public ResponseEntity<PaymentIntentDto> confirmPayment (
            @Parameter(description = "Détails pour la confirmation du paiement", required = true)
            @RequestBody ConfirmPaymentDto confirmPaymentDto) {
        try {
            PaymentIntentDto paymentIntent = stripeService.confirmPaymentIntent(confirmPaymentDto.getPaymentIntentId());
            return ResponseEntity.ok(paymentIntent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
