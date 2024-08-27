package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean isUserExist(String username, String email) {
        return userRepository.findByUsername(username).isPresent() ||
                userRepository.findByEmail(email).isPresent();
    }

    public void registerUser(User user) {

        if (isUserExist(user.getUsername(), user.getEmail())) {
            throw new ValidationException("User with this username or email already exists.");
        }


        if (!isPasswordValid(user.getPassword())) {
            throw new ValidationException("Password does not meet the required criteria.");
        }


        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);


        userRepository.save(user);
    }

    private boolean isPasswordValid(String password) {

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
