package com.revature.unit_tests.service_tests;


import com.revature.repository.*;

import com.revature.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestExpenseService {

    @Mock
    private ExpenseRepository repository1;

    @Mock
    private ApprovalRepository repository2;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;
    private User user;
    private Approval approval;

    // MU-168
    @Test
    void getPendingExpenses_returnsListFromRepository() {
        Expense expense = new Expense(1, 1, 200.0, "Travel", "02/02/2025");
        User user = new User(1, "Jane101", "Jane", "Employee");
        Approval approval = new Approval(1, 1, "pending", 103, "Still Pending", "02/03/2025");

        ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

        when(repository1.findPendingExpensesWithUsers()).thenReturn(List.of(e1));

        List<ExpenseWithUser> ewusers = expenseService.getPendingExpenses();
        assertIterableEquals(List.of(e1), ewusers);
    }

    // MU-169, MU-170
    @ParameterizedTest
    @CsvSource({
            "1, 101, 'approved expense', true",      // MU-169: Positive case
            "-1, 2, 'approved expense', false",      // MU-170: Negative cases
            "0, 2, '', false",
            "999, 2, 'approved expense', false"
    })
    void approveExpense_allScenarios(int expenseId, int managerId, String comment, boolean expectedResult) {
        when(repository2.updateApprovalStatus(anyInt(), anyString(), anyInt(), anyString())).thenReturn(expectedResult);

        boolean result = expenseService.approveExpense(expenseId, managerId, comment);

        assertEquals(expectedResult, result);
        verify(repository2).updateApprovalStatus(expenseId, "approved", managerId, comment);
    }

    // MU-171, MU-172
    @ParameterizedTest
    @CsvSource({
            "1, 101, 'denied expense', true",        // MU-171: Positive case
            "-1, 2, 'denied expense', false",        // MU-172: Negative cases
            "0, 2, ' ', false",
            "999, 2, 'denied expense', false"
    })
    void denyExpense_allScenarios(int expenseId, int managerId, String comment, boolean expectedResult) {
        when(repository2.updateApprovalStatus(anyInt(), anyString(), anyInt(), anyString())).thenReturn(expectedResult);

        boolean result = expenseService.denyExpense(expenseId, managerId, comment);

        assertEquals(expectedResult, result);
        verify(repository2).updateApprovalStatus(expenseId, "denied", managerId, comment);
    }

    @Nested
    class generate_report_ByCategory {

        @BeforeEach
        void setUp() {
            expense = new Expense(1, 1, 200.00, "Travel", "02/02/2025");
            user = new User(1, "Jane101", "Jane", "Employee");
            approval = new Approval(1, 1, "pending", 101, "Still Pending", "02/03/2025");
        }

        // MU-173, MU-174, MU-175
        @ParameterizedTest
        @CsvSource({
                "employee, 1, '', '', ''",                                    // MU-173
                "category, 0, Travel, '', ''",                               // MU-174
                "dateRange, 0, '', '02/02/2025', '02/03/2025'"              // MU-175
        })
        void getExpensesByCategory_returnsListFromRepository(
                String methodType,
                int employeeId,
                String category,
                String startDate,
                String endDate
        ) {
            ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

            List<ExpenseWithUser> ewusers = switch (methodType) {
                case "employee" -> {
                    when(repository1.findExpensesByUser(employeeId)).thenReturn(List.of(e1));
                    yield expenseService.getExpensesByEmployee(employeeId);
                }
                case "category" -> {
                    when(repository1.findExpensesByCategory(category)).thenReturn(List.of(e1));
                    yield expenseService.getExpensesByCategory(category);
                }
                case "dateRange" -> {
                    when(repository1.findExpensesByDateRange(startDate, endDate)).thenReturn(List.of(e1));
                    yield expenseService.getExpensesByDateRange(startDate, endDate);
                }
                default -> throw new IllegalArgumentException("Unknown method type");
            };

            assertIterableEquals(List.of(e1), ewusers);
        }

        // MU-176, MU-177, MU-178
        @ParameterizedTest
        @CsvSource({
                "employee, 1, '', '', ''",                                    // MU-176
                "category, 0, Travel, '', ''",                               // MU-177
                "dateRange, 0, '', '02/02/2025', '02/03/2025'"              // MU-178
        })
        void getExpensesByCategory_throwsException(
                String methodType,
                int employeeId,
                String category,
                String startDate,
                String endDate
        ) {
            switch (methodType) {
                case "employee" -> {
                    when(repository1.findExpensesByUser(anyInt())).thenThrow(new RuntimeException("DB error"));
                    assertThrows(RuntimeException.class, () -> expenseService.getExpensesByEmployee(employeeId));
                }
                case "category" -> {
                    when(repository1.findExpensesByCategory(anyString())).thenThrow(new RuntimeException("DB error"));
                    assertThrows(RuntimeException.class, () -> expenseService.getExpensesByCategory(category));
                }
                case "dateRange" -> {
                    when(repository1.findExpensesByDateRange(anyString(), anyString())).thenThrow(new RuntimeException("DB error"));
                    assertThrows(RuntimeException.class, () -> expenseService.getExpensesByDateRange(startDate, endDate));
                }
            }
        }
    }

    // MU-179
    @Test
    void getAllExpenses_returnsListFromRepository() {
        expense = new Expense(1, 1, 200.00, "Travel", "02/02/2025");
        user = new User(1, "Jane101", "Jane", "Employee");
        approval = new Approval(1, 1, "pending", 101, "Still Pending", "02/03/2025");

        ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

        when(repository1.findAllExpensesWithUsers()).thenReturn(List.of(e1));

        List<ExpenseWithUser> ewusers = expenseService.getAllExpenses();
        assertIterableEquals(List.of(e1), ewusers);
    }

    // MU-180
    @Test
    void testGenerateCsvReportPositive() {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setAmount(2500.0);
        expense.setDescription("Travel Expense");
        expense.setDate("02/02/2025");

        User user = new User();
        user.setUsername("Jane101");

        Approval approval = new Approval();
        approval.setStatus("approved");
        approval.setReviewer(101);
        approval.setComment("Finally Approved");
        approval.setReviewDate("02/03/2025");

        ExpenseWithUser expenseWithUser = new ExpenseWithUser(expense, user, approval);
        List<ExpenseWithUser> ewu = List.of(expenseWithUser);

        String csvResult = expenseService.generateCsvReport(ewu);

        assertTrue(csvResult.contains(
                "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
        ));
        assertTrue(csvResult.contains(
                "1,Jane101,2500.0,Travel Expense,02/02/2025,approved,101,Finally Approved,02/03/2025"
        ));
    }

    // MU-181
    @ParameterizedTest
    @CsvSource({
            "NULL, NULL, NULL",
            "101, NULL, 01/11/2025",
            "NULL, OK, NULL",
            "55, NULL, NULL"
    })
    void generateCsvReport_NullFields(String reviewer, String comment, String review_Date) {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setAmount(2500.0);
        expense.setDescription("Travel Expense");
        expense.setDate("02/02/2025");

        User user = new User();
        user.setUsername("Jane101");

        Approval approval = new Approval();
        approval.setStatus("pending");

        approval.setReviewer("NULL".equals(reviewer) ? null : Integer.valueOf(reviewer));
        approval.setComment("NULL".equals(comment) ? null : comment);
        approval.setReviewDate("NULL".equals(review_Date) ? null : review_Date);

        ExpenseWithUser eWu = new ExpenseWithUser(expense, user, approval);
        List<ExpenseWithUser> ewu = List.of(eWu);

        String csvResult = expenseService.generateCsvReport(ewu);

        assertNotNull(csvResult);
        assertTrue(csvResult.contains("1,Jane101,2500.0,Travel Expense,02/02/2025,pending,"));
    }

    // MU-182
    @Test
    void testCsvEscapingTestPositive() {
        expense = new Expense(1, 1, 250.0, "Hello, \"World\"\nTest", "2025-01-10");
        user = new User(1, "john101", "john", "employee");
        approval = new Approval(1, 1, "approved", 101, "Valid", "2025-01-11");

        ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

        String csv = expenseService.generateCsvReport(List.of(e1));

        assertTrue(csv.contains("\"Hello, \"\"World\"\"\nTest\""));
    }

    // MU-183
    @ParameterizedTest
    @CsvSource({
            "'Lunch', Lunch",
            "'HelloWorld', HelloWorld",
            "'12345', 12345",
            "' ', ' '",
            "'', ''"
    })
    void escapeCsvValue_negativeCases(String input, String expected) {
        Expense expense = new Expense();
        expense.setId(1);
        expense.setAmount(10.00);
        expense.setDescription(input);
        expense.setDate("2025-01-01");

        User user = new User();
        user.setUsername("testuser");

        Approval approval = new Approval();
        approval.setStatus("APPROVED");

        ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

        String csv = expenseService.generateCsvReport(List.of(e1));
        assertTrue(csv.contains(expected));
    }
}