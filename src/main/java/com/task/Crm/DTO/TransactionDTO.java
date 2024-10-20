package com.task.Crm.DTO;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TransactionDTO {
    private int amount;

    private String paymentType;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime transactionDate;

    private SellerDTO seller;
}
