package com.jo.api.dto;

// DTO pour créer un paiement
public class CreatePaymentDto {
    private Long amount;
    private String description;
    private String currency;

    public CreatePaymentDto() {}

    public CreatePaymentDto(Long amount, String description, String currency) {
        this.amount = amount;
        this.description = description;
        this.currency = currency;
    }
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
