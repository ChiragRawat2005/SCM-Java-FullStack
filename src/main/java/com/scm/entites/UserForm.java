package com.scm.entites;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserForm {

    private String id;

    @NotBlank(message = "Name Must Not Be Empty.")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]{2,49}$", message = "Invalid User Name! Must Between 3 - 50 Characters.")
    private String name;

    @Email(message = "Invalid Email Address. [example@gmail.com]")
    @NotBlank(message = "Email Must Not Be Empty.")
    private String email;

    @NotBlank(message = "Password Must Not Be Empty.")
    @Size(min = 6, message = "Password Atleast Contain 6 Characters")
    private String password;

    @NotBlank(message = "Phone Number Must Not Be Empty.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone Number.")
    private String phone;

    @Size(max = 250, message = "Message section must not exceed 250 characters.")
    private String message;
}