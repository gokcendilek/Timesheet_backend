package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    List<Timesheet> findByUserId(Long userId);
    List<Timesheet> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    Optional<Timesheet> findByIdAndUser(Long id, User user);
    List<Timesheet> findByUserIdAndDate(Long userId, LocalDate date);

}


