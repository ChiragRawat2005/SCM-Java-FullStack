package com.scm.entites;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateForm {

    private String id;

    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]{2,49}$", message = "Invalid User Name! Must Between 3 - 50 Characters.")
    private String name;

    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Invalid Phone Number")
    private String phone;

    @Size(max = 250, message = "Message too long")
    private String message;

    @Pattern(regexp = "^$|^.{6,}$", message = "Password must contain at least 6 characters")
    private String newPassword;

    // profile image
    private MultipartFile profilePic;

}