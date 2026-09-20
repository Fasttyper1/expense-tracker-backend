package com.example.demo.service;

import com.example.demo.model.Expense;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendDailySummary(User user, List<Expense> todaysExpenses) {
        double total = todaysExpenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Double> byCategory = todaysExpenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(user.getName()).append(",\n\n");
        body.append("Here's your spending summary for today:\n\n");

        if (todaysExpenses.isEmpty()) {
            body.append("No expenses logged today. \n\n");
        } else {
            for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
                body.append(String.format("- %s: $%.2f%n", entry.getKey(), entry.getValue()));
            }
            body.append(String.format("%nTotal: $%.2f%n%n", total));
        }

        body.append("Keep tracking your expenses!\n");
        body.append("- Your Expense Tracker");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your Daily Expense Summary");
        message.setText(body.toString());

        mailSender.send(message);
    }
}
