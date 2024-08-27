package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.TimesheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TimesheetRepository timesheetRepository;

    @Autowired
    public AdminService(UserRepository userRepository, TimesheetRepository timesheetRepository) {
        this.userRepository = userRepository;
        this.timesheetRepository = timesheetRepository;
    }



public List<User> searchUsers(String username, String email, String registrationDate) {
    if (username != null && !username.isEmpty()) {

        return userRepository.findByUsernameContaining(username);
    }

    if (email != null && !email.isEmpty()) {
        return userRepository.findByEmailContaining(email);
    }

    if (registrationDate != null && !registrationDate.isEmpty()) {
        try {
            LocalDate regDate = LocalDate.parse(registrationDate);
            return userRepository.findByRegistrationDate(regDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please enter a date in yyyy-MM-dd format.");
        }
    }

    return userRepository.findAll();
}




    public List<Timesheet> searchTimesheets(Long userId, String date) {
        if(userId == null && date==null){
            return timesheetRepository.findAll();
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return List.of();
        }

        if (date != null && !date.isEmpty()) {
            LocalDate searchDate = LocalDate.parse(date);
            return timesheetRepository.findByUserIdAndDate(user.get().getId(), searchDate);
        }

        return timesheetRepository.findByUserId(user.get().getId());
    }


    public ByteArrayInputStream exportUsersToCSV() {
        List<User> users = userRepository.findAll();
        String[] header = {"ID", "Username", "Email", "Role"};

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            writer.writeNext(header);

            for (User user : users) {
                String[] data = {
                        String.valueOf(user.getId()),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().toString()
                };
                writer.writeNext(data);
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ByteArrayInputStream exportTimesheetsToCsv(String username, String startDate, String endDate) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return null;
        }

        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : LocalDate.MAX;

        List<Timesheet> timesheets = timesheetRepository.findByUserAndDateBetween(user.get(), start, end);

        String[] header = {"ID", "Date", "Start Time", "End Time", "Description"};

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            writer.writeNext(header);

            for (Timesheet timesheet : timesheets) {
                String[] data = {
                        String.valueOf(timesheet.getId()),
                        timesheet.getDate().toString(),
                        timesheet.getStartTime().toString(),
                        timesheet.getEndTime().toString(),
                        timesheet.getDescription()
                };
                writer.writeNext(data);
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ByteArrayInputStream exportTimesheetsToExcel(String username, String startDate, String endDate) throws IOException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return null;
        }

        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : LocalDate.MAX;

        List<Timesheet> timesheets = timesheetRepository.findByUserAndDateBetween(user.get(), start, end);
        String[] columns = {"ID", "Date", "Start Time", "End Time", "Description"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Timesheets");


            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }


            int rowIdx = 1;
            for (Timesheet timesheet : timesheets) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(timesheet.getId());
                row.createCell(1).setCellValue(timesheet.getDate().toString());
                row.createCell(2).setCellValue(timesheet.getStartTime().toString());
                row.createCell(3).setCellValue(timesheet.getEndTime().toString());
                row.createCell(4).setCellValue(timesheet.getDescription());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
