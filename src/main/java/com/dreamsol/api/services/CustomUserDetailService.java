package com.dreamsol.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dreamsol.api.repositories.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     // Get User From Database based on email
      return userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
    }
    
}
