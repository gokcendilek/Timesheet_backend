package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.service.TimesheetService;
import com.aksigorta.timesheet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/api/timesheets")
public class TimesheetController {

    private final TimesheetService timesheetService;
    private final UserService userService;

    @Autowired
    public TimesheetController(TimesheetService timesheetService, UserService userService) {
        this.timesheetService = timesheetService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Timesheet> createTimesheet(@RequestBody Timesheet timesheet, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        timesheet.setUser(user);
        Timesheet createdTimesheet = timesheetService.saveTimesheet(timesheet);
        return new ResponseEntity<>(createdTimesheet, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Timesheet>> getTimesheets(@RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Timesheet> timesheets = timesheetService.findTimesheets(user, startDate, endDate);
        return new ResponseEntity<>(timesheets, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Timesheet> updateTimesheet(@PathVariable Long id, @RequestBody Timesheet timesheet, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        Timesheet existingTimesheet = timesheetService.findByUserAndTimesheetId(user, id);
        if (existingTimesheet == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        timesheet.setId(id);
        timesheet.setUser(user);
        Timesheet updatedTimesheet = timesheetService.updateTimesheet(timesheet);
        return new ResponseEntity<>(updatedTimesheet, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimesheet(@PathVariable Long id) {
        timesheetService.deleteTimesheet(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<StreamingResponseBody> exportTimesheetsToCsv(@RequestParam(required = false) LocalDate startDate,
                                                                       @RequestParam(required = false) LocalDate endDate,
                                                                       Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Timesheet> timesheets = timesheetService.findTimesheets(user, startDate, endDate);

        if (timesheets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        StreamingResponseBody stream = outputStream -> {
            try (PrintWriter writer = new PrintWriter(outputStream)) {
                writer.println("Date,Start Time,End Time,Description");

                for (Timesheet timesheet : timesheets) {
                    writer.printf("%s,%s,%s,%s%n",
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
