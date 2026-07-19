package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    private String category;
    private Double amount;
    private LocalDate date;
    private String note;
}