package com.subhro.eWallet.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
            @Index( name = "index_email", columnList = "email", unique = true),
            @Index(name = "index_number", columnList = "phoneNumber", unique = true)
        })

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private int phoneNumber;
}
