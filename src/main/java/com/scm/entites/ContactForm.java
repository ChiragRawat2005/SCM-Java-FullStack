package com.scm.entites;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactForm {

    @NotBlank(message = "Name Must Not Be Empty.")
    @Pattern(regexp = "^[A-Za-z][A-Za-z ]{2,49}$", message = "Invalid User Name! Must Between 3 - 50 Characters.")
    private String name;

    @Email(message = "Invalid Email Address. [example@gmail.com]")
    @NotBlank(message = "Email Must Not Be Empty.")
    private String email;

    @NotBlank(message = "Phone Number Must Not Be Empty.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone Number.")
    private String phone;

    @Size(max = 250, message = "Address must not exceed 250 characters.")
    private String address;

    private MultipartFile picture;

    private String picture_url;

    @Size(max = 250, message = "Message must not exceed 250 characters.")
    private String message;

    private boolean favourite;

    private String websiteLink;

    private String linkedInLink;

}