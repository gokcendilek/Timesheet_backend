package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.service.AdminService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String username,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String registrationDate) {
        List<User> users = adminService.searchUsers(username, email, registrationDate);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/timesheets")
    public ResponseEntity<List<Timesheet>> searchTimesheets(@RequestParam(required = false) Long userId,
                                                            @RequestParam(required = false) String date) {
        List<Timesheet> timesheets = adminService.searchTimesheets(userId, date);
        return ResponseEntity.ok(timesheets);
    }

    @GetMapping("/timesheets/export/csv")
    public ResponseEntity<StreamingResponseBody> exportTimesheetsToCsv(@RequestParam(required = false) Long userId,@RequestParam(required = false) String date) {
        List<Timesheet> timesheets = adminService.searchTimesheets(userId, date);

        if (timesheets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        StreamingResponseBody stream = outputStream -> {
            try (PrintWriter writer = new PrintWriter(outputStream)) {
                writer.println("UserName,Date,Start Time,End Time,Description");

                for (Timesheet timesheet : timesheets) {
                    writer.printf("%s,%s,%s,%s,%s%n",
                            timesheet.getUser().getUsername(),
                            timesheet.getDate(),
                            timesheet.getStartTime(),
                            timesheet.getEndTime(),
                            timesheet.getDescription());
                }
                writer.flush();
            }
        };

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=timesheets.csv")
                .body(stream);
    }
}
