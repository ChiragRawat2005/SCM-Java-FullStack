package com.scm.entites;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactInfoResponse {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String message;
    private boolean favourite;
    private String websiteLink;
    private String linkedInLink;
    private String picture;

}