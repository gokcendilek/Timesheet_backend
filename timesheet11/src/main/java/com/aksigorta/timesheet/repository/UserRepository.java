package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String email);
    List<User> findByRegistrationDate(LocalDate registrationDate);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findByUsernameContaining(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<User> findByEmailContaining(@Param("email") String email);



}

