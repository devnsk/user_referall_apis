package com.dev.userregisterreferall.controller;

import com.dev.userregisterreferall.dto.ReferralRequest;
import com.dev.userregisterreferall.dto.UserDto;
import com.dev.userregisterreferall.entity.User;
import com.dev.userregisterreferall.repository.UserRepository;
import com.dev.userregisterreferall.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            User newUser = userService.registerUser(userDto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generate-referral")
    public ResponseEntity<String> generateReferralCode(@RequestParam String username) {
        try {
            String referralCode = userService.generateReferralCode(username);
            return new ResponseEntity<>("Your referral code is: " + referralCode, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register-with-referral")
    public ResponseEntity<?> registerWithReferral(@Valid @RequestBody ReferralRequest referralRequest) {
        try {
            UserDto userDto = new UserDto();
            userDto.setUsername(referralRequest.getUsername());
            userDto.setPassword(referralRequest.getPassword());
            userDto.setEmail(referralRequest.getEmail());
            userDto.setReferralCode(referralRequest.getReferralCode());
            User newUser = userService.registerUser(userDto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String username) {
        return userService.getProfile(username)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
