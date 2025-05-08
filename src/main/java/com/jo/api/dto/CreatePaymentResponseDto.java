package com.jo.api.dto;

// DTO pour la réponse de création de paiement
public class CreatePaymentResponseDto {
    private String clientSecret;
    private String error;

    public CreatePaymentResponseDto() {}

    public CreatePaymentResponseDto(String clientSecret, String error) {
        this.clientSecret = clientSecret;
        this.error = error;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
