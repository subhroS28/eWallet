package com.subhro.eWallet.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    private String purpose;
    private Double amount;

    @CreationTimestamp
    private Date transactionDate;
    private String sender;
    private String receiver;

}
