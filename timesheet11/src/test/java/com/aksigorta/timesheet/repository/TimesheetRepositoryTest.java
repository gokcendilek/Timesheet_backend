package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TimesheetRepositoryTest {

    @MockBean
    private TimesheetRepository timesheetRepository;

    @Test
    public void testFindByUserId() {
        Timesheet ts = new Timesheet();
        ts.setId(1L);
        when(timesheetRepository.findByUserId(1L)).thenReturn(Collections.singletonList(ts));

        List<Timesheet> results = timesheetRepository.findByUserId(1L);

        assertFalse(results.isEmpty());
        assertEquals(1L, results.get(0).getId());
        verify(timesheetRepository).findByUserId(1L);
    }

    @Test
    public void testFindByUserAndDateBetween() {
        User user = new User();
        user.setId(1L);
        Timesheet ts = new Timesheet();
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        when(timesheetRepository.findByUserAndDateBetween(user, start, end)).thenReturn(Arrays.asList(ts));

        List<Timesheet> results = timesheetRepository.findByUserAndDateBetween(user, start, end);

        assertFalse(results.isEmpty());
        verify(timesheetRepository).findByUserAndDateBetween(user, start, end);
    }

    @Test
    public void testFindByIdAndUser() {
        User user = new User();
        user.setId(1L);
        Timesheet ts = new Timesheet();
        ts.setId(1L);
        when(timesheetRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(ts));

        Optional<Timesheet> result = timesheetRepository.findByIdAndUser(1L, user);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(timesheetRepository).findByIdAndUser(1L, user);
    }

    @Test
    public void testFindByUserIdAndDate() {
        Timesheet ts = new Timesheet();
        ts.setId(1L);
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(timesheetRepository.findByUserIdAndDate(1L, date)).thenReturn(Collections.singletonList(ts));

        List<Timesheet> results = timesheetRepository.findByUserIdAndDate(1L, date);

        assertFalse(results.isEmpty());
        assertEquals(1L, results.get(0).getId());
        verify(timesheetRepository).findByUserIdAndDate(1L, date);
    }
}

