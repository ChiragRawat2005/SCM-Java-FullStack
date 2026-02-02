package com.scm.services.Impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entites.Providers;
import com.scm.entites.User;
import com.scm.helpers.Helper;
import com.scm.repository.UserRepository;
import com.scm.services.EmailService;
import com.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    public BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public EmailService emailService;

    @Override
    public User saveUser(User user) {

        user.setId(UUID.randomUUID().toString());

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        user.setRoleList(List.of("USER"));

        user.setEnabled(false);

        user.setProfilePic(null);

        user.setProvider(Providers.SELF);

        String emailToken = UUID.randomUUID().toString();
        user.setEmailToken(emailToken);

        User savedUser = repo.save(user);

        // Send verification mail
        String emailLink = Helper.getLinkForEmailVerification(emailToken);
        emailService.sendEmail(
                savedUser.getEmail(),
                "Email Verification Link : Smart Contact Manager",
                emailLink);

        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return repo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {

        // user is already fetched from DB in controller
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updatedUser = repo.save(user);
        return Optional.of(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        repo.deleteById(id);
    }

    @Override
    public boolean isUserExist(String id) {
        User user = repo.findById(id).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = repo.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return this.repo.findByEmail(email).orElse(null);
    }

    @Override
    public boolean emailExists(String email) {
        return this.repo.existsByEmail(email);
    }

}