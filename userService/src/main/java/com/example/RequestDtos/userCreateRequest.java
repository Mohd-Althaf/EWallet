package com.example.RequestDtos;


import com.example.Model.user;
import com.example.userIdentifier;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class userCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNo;

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String dob;
    private String country;
    @NotBlank
    private userIdentifier userIdentifier;
    @NotBlank
    private String identifierValue;
    public user toUser(){
        return user.builder()
                .country(this.country)
                .dob(this.dob)
                .email(this.email)
                .phoneNo(this.phoneNo)
                .password(this.password)
                .name(this.name)
                .identifierValue(this.identifierValue)
                .userIdentifier(this.userIdentifier)
                .build();
    }
}
