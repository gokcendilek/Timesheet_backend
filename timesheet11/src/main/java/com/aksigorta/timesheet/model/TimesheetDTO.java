package com.aksigorta.timesheet.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class TimesheetDTO {

    @Column(nullable = false)
    private String description;
}
