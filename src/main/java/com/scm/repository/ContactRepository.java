package com.scm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entites.Contact;
import com.scm.entites.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    Page<Contact> findByUser(User user, Pageable pageable);

    @Query("SELECT c From Contact c Where c.user.id = :userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    Page<Contact> findByUserAndNameStartingWithIgnoreCase(User user, String name, Pageable pageable);

    Page<Contact> findByUserAndEmailStartingWithIgnoreCase(User user, String email, Pageable pageable);

    Page<Contact> findByUserAndPhoneStartingWith(User user, String phone, Pageable pageable);

    long countByUser(User user);

    long countByUserAndFavouriteTrue(User user);

}