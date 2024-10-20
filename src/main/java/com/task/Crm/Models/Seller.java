package com.task.Crm.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "seller")
public class Seller {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @Column(name = "contact_info")
    @NotEmpty(message = "Contact info should not be empty")
    private String contact_info;

    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Registration date should not be empty")
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Transaction> transactionList;

    public Seller() {}

    public Seller(int id, String name, String contactInfo, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.contact_info = contactInfo;
        this.registrationDate = registrationDate;
    }

    public Seller(String name, String contactInfo, LocalDateTime registrationDate) {
        this.name = name;
        this.contact_info = contactInfo;
        this.registrationDate = registrationDate;
    }

    public Seller(String contact_info, String name) {
        this.contact_info = contact_info;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Seller { " +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactInfo='" + contact_info + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                "}\n";
    }
}
