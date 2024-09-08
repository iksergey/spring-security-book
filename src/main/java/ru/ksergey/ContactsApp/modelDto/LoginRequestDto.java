package ru.ksergey.ContactsApp.modelDto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
