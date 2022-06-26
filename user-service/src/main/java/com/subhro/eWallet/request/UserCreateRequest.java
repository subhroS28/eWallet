package com.subhro.eWallet.request;

import com.subhro.eWallet.model.User;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private String name;
    private String email;
    private int phoneNumber;

    public User toUser(){
        return User.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }
}
