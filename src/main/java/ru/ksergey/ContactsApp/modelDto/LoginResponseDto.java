package ru.ksergey.ContactsApp.modelDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String email;
    private String jwtToken;
    private String refreshToken;
}
