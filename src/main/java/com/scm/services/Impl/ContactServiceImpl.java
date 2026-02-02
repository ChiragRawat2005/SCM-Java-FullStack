package com.scm.services.Impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.entites.Contact;
import com.scm.entites.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repository.ContactRepository;
import com.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    public ContactRepository repo;

    @Override
    public Contact saveContact(Contact contact) {
        if (contact.getId() == null) {
            contact.setId(UUID.randomUUID().toString());
        }
        return this.repo.save(contact);
    }

    @Override
    public Contact getContactById(String id) {
        return this.repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact Not Found By given id"));
    }

    @Override
    public Contact updateContact(Contact contact) {
        var contactOld = this.repo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhone(contact.getPhone());
        contactOld.setAddress(contact.getAddress());
        contactOld.setMessage(contact.getMessage());
        contactOld.setPicture(contact.getPicture());
        contactOld.setFavourite(contact.isFavourite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());

        // Update image ONLY if new image is provided DB
        // if (contact.getPicture() != null && contact.getPicture().length > 0) {
        // contactOld.setPicture(contact.getPicture());
        // contactOld.setPicture_type(contact.getPicture_type());
        // }

        return this.repo.save(contactOld);
    }

    @Override
    public void deleteContact(String id) {
        this.repo.deleteById(id);
    }

    @Override
    public List<Contact> getAllContact() {
        return this.repo.findAll();
    }

    @Override
    public Page<Contact> searchContacts(User user, String field, String keyword, int page, int size, String sortBy,
            String order) {

        if (field == null || keyword == null || keyword.isEmpty()) {
            return Page.empty(); // empty result
        }

        Sort sort = order.equals("decs") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        switch (field.toLowerCase()) {

            case "name":
                return repo.findByUserAndNameStartingWithIgnoreCase(user, keyword.toLowerCase(), pageable);

            case "email":
                return repo.findByUserAndEmailStartingWithIgnoreCase(user, keyword.toLowerCase(), pageable);

            case "phone":
                return repo.findByUserAndPhoneStartingWith(user, keyword.toLowerCase(), pageable);

            default:
                return Page.empty();
        }
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return this.repo.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortby, String direction) {
        Sort sort = direction.equals("desc") ? Sort.by(sortby).descending() : Sort.by(sortby).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return this.repo.findByUser(user, pageable);
    }

    @Override
    public long countContactsByUser(User user) {
        return repo.countByUser(user);
    }

    @Override
    public long countFavouriteContactsByUser(User user) {
        return repo.countByUserAndFavouriteTrue(user);
    }
}