package com.example.demo;

import com.example.demo.model.Expense;
import com.example.demo.model.User;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DailyReportScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EmailService emailService;

    // Runs every day at 8:00 PM server time
    @Scheduled(cron = "0 0 20 * * *")
    public void sendDailyReports() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
            List<Expense> todaysExpenses = expenseRepository.findByUserAndDate(user, today);
            emailService.sendDailySummary(user, todaysExpenses);
        }
    }
}