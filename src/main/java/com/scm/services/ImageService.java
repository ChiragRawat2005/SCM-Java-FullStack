package com.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    public String uploadImage(MultipartFile image, String filename);

    public String getUrlFromPublic(String publicId);

    public void deleteImage(String publicId);

}