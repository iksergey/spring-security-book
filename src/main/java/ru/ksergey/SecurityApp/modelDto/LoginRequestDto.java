package ru.ksergey.SecurityApp.modelDto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
