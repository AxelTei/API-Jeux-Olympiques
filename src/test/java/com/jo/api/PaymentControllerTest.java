package com.jo.api;

import com.jo.api.dto.ConfirmPaymentDto;
import com.jo.api.dto.CreatePaymentDto;
import com.jo.api.dto.CreatePaymentResponseDto;
import com.jo.api.dto.PaymentIntentDto;
import com.jo.api.service.StripeService;
import com.jo.api.ws.PaymentController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void testCreatePaymentIntent_Success() {
        // Préparation des données de test
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setAmount((long) 100.00);
        createPaymentDto.setCurrency("EUR");
        createPaymentDto.setDescription("Test Payment");

        CreatePaymentResponseDto expectedResponse = new CreatePaymentResponseDto("pi_123456789", null);

        when(stripeService.createPaymentIntent(createPaymentDto)).thenReturn(expectedResponse);

        // Exécution du test
        ResponseEntity<CreatePaymentResponseDto> response = paymentController.createPaymentIntent(createPaymentDto);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getClientSecret()).isEqualTo("pi_123456789");
        assertThat(response.getBody().getError()).isNull();
    }

    @Test
    void testCreatePaymentIntent_Error() {
        // Préparation des données de test
        CreatePaymentDto createPaymentDto = new CreatePaymentDto();
        createPaymentDto.setAmount((long) -100.00); // Montant négatif pour provoquer une erreur

        when(stripeService.createPaymentIntent(createPaymentDto))
                .thenThrow(new IllegalArgumentException("Le montant ne peut pas être négatif"));

        // Exécution du test
        ResponseEntity<CreatePaymentResponseDto> response = paymentController.createPaymentIntent(createPaymentDto);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getClientSecret()).isNull();
        assertThat(response.getBody().getError()).isEqualTo("Le montant ne peut pas être négatif");
    }

    @Test
    void testGetPaymentIntent_Success() {
        // Préparation des données de test
        String paymentIntentId = "pi_123456789";
        PaymentIntentDto expectedPaymentIntent = new PaymentIntentDto();
        expectedPaymentIntent.setId(paymentIntentId);
        expectedPaymentIntent.setAmount((long) 100.00);
        expectedPaymentIntent.setStatus("succeeded");

        when(stripeService.getPaymentIntent(paymentIntentId)).thenReturn(expectedPaymentIntent);

        // Exécution du test
        ResponseEntity<PaymentIntentDto> response = paymentController.getPaymentIntent(paymentIntentId);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(paymentIntentId);
        assertThat(response.getBody().getAmount()).isEqualTo((long) 100.00);
        assertThat(response.getBody().getStatus()).isEqualTo("succeeded");
    }

    @Test
    void testGetPaymentIntent_Error() {
        // Préparation des données de test
        String invalidPaymentIntentId = "invalid_id";

        when(stripeService.getPaymentIntent(invalidPaymentIntentId))
                .thenThrow(new RuntimeException("PaymentIntent non trouvé"));

        // Exécution du test
        ResponseEntity<PaymentIntentDto> response = paymentController.getPaymentIntent(invalidPaymentIntentId);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testConfirmPayment_Success() {
        // Préparation des données de test
        String paymentIntentId = "pi_123456789";
        ConfirmPaymentDto confirmPaymentDto = new ConfirmPaymentDto();
        confirmPaymentDto.setPaymentIntentId(paymentIntentId);

        PaymentIntentDto expectedPaymentIntent = new PaymentIntentDto();
        expectedPaymentIntent.setId(paymentIntentId);
        expectedPaymentIntent.setAmount((long) 100.00);
        expectedPaymentIntent.setStatus("succeeded");

        when(stripeService.confirmPaymentIntent(paymentIntentId)).thenReturn(expectedPaymentIntent);

        // Exécution du test
        ResponseEntity<PaymentIntentDto> response = paymentController.confirmPayment(confirmPaymentDto);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(paymentIntentId);
        assertThat(response.getBody().getStatus()).isEqualTo("succeeded");
    }

    @Test
    void testConfirmPayment_Error() {
        // Préparation des données de test
        String invalidPaymentIntentId = "invalid_id";
        ConfirmPaymentDto confirmPaymentDto = new ConfirmPaymentDto();
        confirmPaymentDto.setPaymentIntentId(invalidPaymentIntentId);

        when(stripeService.confirmPaymentIntent(invalidPaymentIntentId))
                .thenThrow(new RuntimeException("PaymentIntent non trouvé ou ne peut pas être confirmé"));

        // Exécution du test
        ResponseEntity<PaymentIntentDto> response = paymentController.confirmPayment(confirmPaymentDto);

        // Vérification des résultats
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }
}
