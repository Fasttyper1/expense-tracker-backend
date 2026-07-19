package com.example.demo.controller;

import com.example.demo.dto.ExpenseRequest;
import com.example.demo.model.Expense;
import com.example.demo.model.User;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // Helper: get the currently logged-in user from the JWT
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Expense expense = new Expense();
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setNote(request.getNote());
        expense.setUser(user);

        expenseRepository.save(expense);
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<?> getExpenses(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Expense> expenses = expenseRepository.findByUser(user);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id,
                                           @RequestBody ExpenseRequest request,
                                           Authentication authentication) {
        User user = getCurrentUser(authentication);

        Expense expense = expenseRepository.findById(id)
                .orElse(null);

        if (expense == null || !expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }

        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setNote(request.getNote());

        expenseRepository.save(expense);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Expense expense = expenseRepository.findById(id)
                .orElse(null);

        if (expense == null || !expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }

        expenseRepository.delete(expense);
        return ResponseEntity.ok(Map.of("message", "Expense deleted"));
    }
}