package com.example.Model;


import com.example.userIdentifier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Long userId;
    private String phoneNo;
    private Double balance;

    @Enumerated(EnumType.STRING)
    private userIdentifier userIdentifier;

    private String identifierValue;


}
