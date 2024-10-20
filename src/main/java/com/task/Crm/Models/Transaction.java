package com.task.Crm.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "amount")
    private int amount;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime transactionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Seller seller;

    public Transaction(int amount, String paymentType, LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    public Transaction(int id, int amount, String paymentType, LocalDateTime transactionDate) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    public Transaction() {}

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentType='" + paymentType + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
