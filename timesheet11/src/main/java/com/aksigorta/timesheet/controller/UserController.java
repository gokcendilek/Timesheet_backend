package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.Role;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.security.JwtUtil;
import com.aksigorta.timesheet.service.UserService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            if (userService.isUserExist(user.getUsername(), user.getEmail())) {
                return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (user.getRole() == null) {
                user.setRole(Role.USER);
            }

            userService.saveUser(user);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User loginUser) {
        User user = userService.findByUsername(loginUser.getUsername());
        if (user == null || !passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
