package com.example.demo.security.services;

import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Assume you have a UserRepository and User entity in your application
// Uncomment and adjust the imports and code according to your actual implementation
//import com.example.demo.repositories.UserRepository;
//import com.example.demo.entities.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Uncomment when your User entity and repository are available
    /*
    @Autowired
    private UserRepository userRepository;
    */

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Replace this with your actual user lookup logic when your User entity and repository are available
        /*
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return UserDetailsImpl.build(user);
        */

        // This is a placeholder implementation that should be replaced with actual database lookup
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
} 