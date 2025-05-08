package com.jo.api.ws;

import com.jo.api.dto.ConfirmPaymentDto;
import com.jo.api.dto.CreatePaymentDto;
import com.jo.api.dto.CreatePaymentResponseDto;
import com.jo.api.dto.PaymentIntentDto;
import com.jo.api.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ApiRegistration.API + ApiRegistration.REST_PAYMENT)
public class PaymentController {

    private final StripeService stripeService;

    @Autowired
    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<CreatePaymentResponseDto> createPaymentIntent (@RequestBody CreatePaymentDto createPaymentDto) {
        try {
            CreatePaymentResponseDto response = stripeService.createPaymentIntent(createPaymentDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CreatePaymentResponseDto(null, e.getMessage()));
        }
    }

    @GetMapping("/payment-intent/{id}")
    public ResponseEntity<PaymentIntentDto> getPaymentIntent (@PathVariable String id) {
        try {
            PaymentIntentDto paymentIntent = stripeService.getPaymentIntent(id);
            return ResponseEntity.ok(paymentIntent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //endpoint pour confirmer le paiement sans passer par Stripe (simulation total)
    @PostMapping("/confirm-payment")
    public ResponseEntity<PaymentIntentDto> confirmPayment (@RequestBody ConfirmPaymentDto confirmPaymentDto) {
        try {
            PaymentIntentDto paymentIntent = stripeService.confirmPaymentIntent(confirmPaymentDto.getPaymentIntentId());
            return ResponseEntity.ok(paymentIntent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
