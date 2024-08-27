package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.TimesheetRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Service
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;

    @Autowired
    public TimesheetService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    public Timesheet saveTimesheet(Timesheet timesheet) {
        return timesheetRepository.save(timesheet);
    }

    public List<Timesheet> findTimesheetsByUserId(Long userId) {
        return timesheetRepository.findByUserId(userId);
    }

    public Timesheet updateTimesheet(Timesheet timesheet) {
        return timesheetRepository.save(timesheet);
    }

    public Timesheet findByUserAndTimesheetId(User user, Long timesheetId) {
        return timesheetRepository.findByIdAndUser(timesheetId, user)
                .orElse(null);
    }

    public void deleteTimesheet(Long id) {
        timesheetRepository.deleteById(id);
    }

    public StreamingResponseBody exportTimesheetsToCsv(User user, LocalDate startDate, LocalDate endDate) {
        List<Timesheet> timesheets = findTimesheetsByUserAndDateRange(user, startDate, endDate);

        return outputStream -> {
            PrintWriter writer = new PrintWriter(outputStream);
            writer.println("Date,Start Time,End Time,Description");

            for (Timesheet timesheet : timesheets) {
                writer.printf("%s,%s,%s,%s%n",
                        timesheet.getDate(),
                        timesheet.getStartTime(),
                        timesheet.getEndTime(),
                        timesheet.getDescription());
            }
            writer.flush();
        };
    }

    public List<Timesheet> findTimesheets(User user, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return timesheetRepository.findByUserId(user.getId());
        }
        return timesheetRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    public List<Timesheet> findTimesheetsByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return timesheetRepository.findByUserAndDateBetween(user, startDate, endDate);
    }


    public StreamingResponseBody exportTimesheetsToExcel(User user, LocalDate startDate, LocalDate endDate) {
        List<Timesheet> timesheets = findTimesheetsByUserAndDateRange(user, startDate, endDate);

        return outputStream -> {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Timesheets");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Start Time");
            headerRow.createCell(2).setCellValue("End Time");
            headerRow.createCell(3).setCellValue("Description");


            int rowNum = 1;
            for (Timesheet timesheet : timesheets) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(timesheet.getDate().toString());
                row.createCell(1).setCellValue(timesheet.getStartTime().toString());
                row.createCell(2).setCellValue(timesheet.getEndTime().toString());
                row.createCell(3).setCellValue(timesheet.getDescription());
            }

            workbook.write(outputStream);
            workbook.close();
        };
    }
}
