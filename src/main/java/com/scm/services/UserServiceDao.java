package com.scm.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scm.entites.User;
import com.scm.entites.UserPrincipal;
import com.scm.repository.UserRepository;

@Service
public class UserServiceDao implements UserDetailsService {

    @Autowired
    public UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> user = repo.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found");
        }

        return new UserPrincipal(user.get());
    }
}