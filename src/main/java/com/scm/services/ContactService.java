package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.scm.entites.Contact;
import com.scm.entites.User;

public interface ContactService {

    Contact saveContact(Contact contact);

    Contact getContactById(String id);

    Contact updateContact(Contact contact);

    void deleteContact(String id);

    List<Contact> getAllContact();

    Page<Contact> searchContacts(User user, String field, String keyword, int page, int size, String sortBy,
            String order);

    List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);

    long countContactsByUser(User user);

    long countFavouriteContactsByUser(User user);

}
