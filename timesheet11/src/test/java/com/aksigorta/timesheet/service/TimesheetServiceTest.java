package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.TimesheetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TimesheetServiceTest {

    @Mock
    private TimesheetRepository timesheetRepository;

    @InjectMocks
    private TimesheetService timesheetService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveTimesheet() {
        Timesheet timesheet = new Timesheet();
        when(timesheetRepository.save(timesheet)).thenReturn(timesheet);

        Timesheet savedTimesheet = timesheetService.saveTimesheet(timesheet);

        assertNotNull(savedTimesheet);
        verify(timesheetRepository, times(1)).save(timesheet);
    }

    @Test
    public void testFindTimesheetsByUserId() {
        Long userId = 1L;
        List<Timesheet> timesheets = new ArrayList<>();
        when(timesheetRepository.findByUserId(userId)).thenReturn(timesheets);

        List<Timesheet> result = timesheetService.findTimesheetsByUserId(userId);

        assertNotNull(result);
        assertEquals(timesheets, result);
        verify(timesheetRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testUpdateTimesheet() {
        Timesheet timesheet = new Timesheet();
        when(timesheetRepository.save(timesheet)).thenReturn(timesheet);

        Timesheet updatedTimesheet = timesheetService.updateTimesheet(timesheet);

        assertNotNull(updatedTimesheet);
        verify(timesheetRepository, times(1)).save(timesheet);
    }

    @Test
    public void testFindByUserAndTimesheetId() {
        User user = new User();
        user.setId(1L);
        Timesheet timesheet = new Timesheet();
        when(timesheetRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(timesheet));

        Timesheet result = timesheetService.findByUserAndTimesheetId(user, 1L);

        assertNotNull(result);
        verify(timesheetRepository, times(1)).findByIdAndUser(1L, user);
    }

    @Test
    public void testDeleteTimesheet() {
        Long timesheetId = 1L;

        timesheetService.deleteTimesheet(timesheetId);

        verify(timesheetRepository, times(1)).deleteById(timesheetId);
    }

    @Test
    public void testExportTimesheetsToCsv() throws IOException {
        User user = new User();
        user.setId(1L);
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Timesheet> timesheets = new ArrayList<>();
        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.now());
        timesheet.setStartTime(LocalTime.of(9, 0)); // LocalTime kullanıyoruz
        timesheet.setEndTime(LocalTime.of(17, 0));  // LocalTime kullanıyoruz
        timesheet.setDescription("Worked on project X.");
        timesheets.add(timesheet);

        when(timesheetRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(timesheets);

        StreamingResponseBody responseBody = timesheetService.exportTimesheetsToCsv(user, startDate, endDate);

        OutputStream outputStream = new ByteArrayOutputStream();
        responseBody.writeTo(outputStream);

        String csvOutput = outputStream.toString();

        assertTrue(csvOutput.contains("Worked on project X."));
        verify(timesheetRepository, times(1)).findByUserAndDateBetween(user, startDate, endDate);
    }

    @Test
    public void testExportTimesheetsToExcel() throws IOException {
        User user = new User();
        user.setId(1L);
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Timesheet> timesheets = new ArrayList<>();
        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.now());
        timesheet.setStartTime(LocalTime.of(9, 0)); // LocalTime kullanıyoruz
        timesheet.setEndTime(LocalTime.of(17, 0));  // LocalTime kullanıyoruz
        timesheet.setDescription("Worked on project Y.");
        timesheets.add(timesheet);

        when(timesheetRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(timesheets);

        StreamingResponseBody responseBody = timesheetService.exportTimesheetsToExcel(user, startDate, endDate);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseBody.writeTo(outputStream);

        assertTrue(outputStream.size() > 0);  // Excel dosyasının oluşturulduğunu kontrol et
        verify(timesheetRepository, times(1)).findByUserAndDateBetween(user, startDate, endDate);
    }
}
