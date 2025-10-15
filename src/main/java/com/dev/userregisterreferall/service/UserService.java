package com.dev.userregisterreferall.service;

import com.dev.userregisterreferall.dto.UserDto;
import com.dev.userregisterreferall.entity.User;
import com.dev.userregisterreferall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    private static final int REFERRAL_POINTS = 10; // Points awarded for a successful referral

    public User registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }

        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(userDto.getPassword());
        newUser.setEmail(userDto.getEmail());

        // Handle referral code
        if (userDto.getReferralCode() != null && !userDto.getReferralCode().isEmpty()) {
            Optional<User> referrerOptional = userRepository.findByReferralCode(userDto.getReferralCode());
            if (referrerOptional.isPresent()) {
                User referrer = referrerOptional.get();
                referrer.setPoints(referrer.getPoints() + REFERRAL_POINTS);
                userRepository.save(referrer);
            } else {
                throw new RuntimeException("Invalid referral code!");
            }
        }

        return userRepository.save(newUser);
    }

    public String generateReferralCode(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getReferralCode() == null || user.getReferralCode().isEmpty()) {
            String referralCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            user.setReferralCode(referralCode);
            userRepository.save(user);
        }
        return user.getReferralCode();
    }

    public Optional<User> getProfile(String username) {
        return userRepository.findByUsername(username);
    }
}
