package com.test.baxolash.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestCreateDto {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 100)
    private String subject;

    @NotBlank(message = "Сообщение обязательно")
    @Size(max = 5000)
    private String message;
}
