package com.revature.service_tests;


import com.revature.repository.*;

import java.io.StringWriter;

import com.revature.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    // verifies correct method is called and return values are passed through unchanged.
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

    @Test
    // MU-169
    void approveExpense_updateApprovalRepositoryPositive() {
        when(repository2.updateApprovalStatus(1, "approved", 101, "approved expense")).thenReturn(true);

        boolean result = expenseService.approveExpense(1, 101, "approved expense");

        assertTrue(result);
        verify(repository2).updateApprovalStatus(1, "approved", 101, "approved expense");

    }
    // MU-170
    @ParameterizedTest
    @CsvSource({
            "-1, 2, 'approved expense'",
            "0, 2, '' ",
            "999, 2, 'approved expense'"
    })
    void approveExpense_updateApprovalRepositoryNegative(
            int expenseId,
            int managerId,
            String comment
    ) {
        when(repository2.updateApprovalStatus(anyInt(), anyString(), anyInt(), anyString())).thenReturn(false);
        boolean result = expenseService.approveExpense(expenseId, managerId, comment);

        assertFalse(result);
        verify(repository2).updateApprovalStatus(expenseId, "approved", managerId, comment);

    }
    // MU 171
    @Test
    void denyExpense_updateApprovalRepositoryPositive() {
        when(repository2.updateApprovalStatus(1, "denied", 101, "denied expense")).thenReturn(true);

        boolean result = expenseService.denyExpense(1, 101, "denied expense");
        assertTrue(result);
        verify(repository2).updateApprovalStatus(1, "denied", 101, "denied expense");
    }

    // MU 172
    @ParameterizedTest
    @CsvSource({
            " -1, 2, 'denied expense' ",
            " 0, 2, ' ' ",
            " 999, 2, 'denied expense' "
    })
    void denyExpense_updateApprovalRepositoryNegative(
            int expenseId,
            int managerId,
            String comment) {
        when(repository2.updateApprovalStatus(anyInt(), anyString(), anyInt(), anyString())).thenReturn(false);
        boolean result = expenseService.denyExpense(expenseId, managerId, comment);

        assertFalse(result);
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

        // Positive get by Employee Category
        // MU 173
        @Test
        void getExpenseByEmployee_returnsListFromRepository() {
            ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);
            when(repository1.findExpensesByUser(1)).thenReturn(List.of(e1));
            List<ExpenseWithUser> ewusers = expenseService.getExpensesByEmployee(1);
            assertIterableEquals(List.of(e1), ewusers);
        }

        // Positive get by Description Category
        // MU 174
        @Test
        void getExpensesByCategory_returnsListFromRepository() {
            ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);
            when(repository1.findExpensesByCategory("Travel")).thenReturn(List.of(e1));
            List<ExpenseWithUser> ewusers = expenseService.getExpensesByCategory("Travel");
            assertIterableEquals(List.of(e1), ewusers);

        }

        // Positive get by DateRange Category
        // MU 175
        @Test
        void getExpensesByDateRange_returnsListFromRepository() {
            ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);
            when(repository1.findExpensesByDateRange("02/02/2025", "02/03/2025")).thenReturn(List.of(e1));
            List<ExpenseWithUser> ewusers = expenseService.getExpensesByDateRange("02/02/2025", "02/03/2025");
            assertIterableEquals(List.of(e1), ewusers);
        }

        // Negative get by Employee Category
        // MU 176
        @Test
        void getExpenseByEmployee_throwsException() {
            when(repository1.findExpensesByUser(anyInt())).thenThrow(new RuntimeException("DB error"));
            assertThrows(RuntimeException.class, () -> expenseService.getExpensesByEmployee(1));
        }

        // Negative get by Description Category
        // MU 177
        @Test
        void getExpenseByCategory_throwsException() {
            when(repository1.findExpensesByCategory(anyString())).thenThrow(new RuntimeException("DB error"));
            assertThrows(RuntimeException.class, () -> expenseService.getExpensesByCategory("Travel"));
        }

        // Negative get by Date Range Category
        // MU 178
        @Test
        void getExpensesByDateRange_throwsException() {
            when(repository1.findExpensesByDateRange(anyString(), anyString())).thenThrow(new RuntimeException("DB error"));
            assertThrows(RuntimeException.class, () -> expenseService.getExpensesByDateRange("02/02/2025", "02/03/2025"));
        }
    }

    // MU 179
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


    // MU 180
    @Test
    void testGenerateCsvReportPositive() {

        // Arrange
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

        ExpenseWithUser expenseWithUser =
                new ExpenseWithUser(expense, user, approval);

        List<ExpenseWithUser> ewu = List.of(expenseWithUser);
        // Act
        String csvResult = expenseService.generateCsvReport(ewu);

        // Assert (verify CSV output)
        // Check CSV header
        assertTrue(csvResult.contains(
                "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
        ));
        // Check CSV data row
        assertTrue(csvResult.contains(
                "1,Jane101,2500.0,Travel Expense,02/02/2025,approved,101,Finally Approved,02/03/2025"
        ));

    }

    // MU 181
    @ParameterizedTest
    @CsvSource({
            // REVIEWER, COMMENT, REVIEW_DATE
            "NULL, NULL, NULL ",
            "101, NULL, 01/11/2025",
            "NULL, OK, NULL",
            "55, NULL, NULL"
    })
    void generateCsvReport_NullFields(
           String reviewer,
           String comment,
           String review_Date
    ){
        //Arrange
        Expense expense = new Expense();
        expense.setId(1);
        expense.setAmount(2500.0);
        expense.setDescription("Travel Expense");
        expense.setDate("02/02/2025");

        User user = new User();
        user.setUsername("Jane101");

        Approval approval = new Approval();
        approval.setStatus("pending");

        // Convert CSV inputs to real values

        approval.setReviewer("NULL".equals(reviewer)
            ? null: Integer.valueOf(reviewer));

        approval.setComment("NULL".equals(comment)
                ? null: comment);
        approval.setReviewDate("NULL".equals(review_Date)
                ? null: review_Date);

        ExpenseWithUser eWu = new ExpenseWithUser(expense, user, approval);

        List<ExpenseWithUser> ewu = List.of(eWu);
        String csvResult = expenseService.generateCsvReport(ewu);

        assertNotNull(csvResult);
        assertTrue(csvResult.contains(
           "1,Jane101,2500.0,Travel Expense,02/02/2025,pending,"
        ));
    }

    // MU 182
    @Test
    void testCsvEscapingTestPositive() {
        expense = new Expense(1, 1, 250.0, "Hello, \"World\"\nTest", "2025-01-10");
        user = new User(1, "john101", "john", "employee");
        approval = new Approval(1, 1, "approved", 101, "Valid", "2025-01-11");

        ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

        String csv = expenseService.generateCsvReport(List.of(e1));

        assertTrue(csv.contains("\"Hello, \"\"World\"\"\nTest\""));
    }


    // MU 183
    @ParameterizedTest
    @CsvSource({
            "'Lunch', Lunch",
            "'HelloWorld', HelloWorld",
            "'12345', 12345",
            "' ', ' '",
            "'', ''"
    })
    void escapeCsvValue_negativeCases(String input, String expected) {

        // Expense
        Expense expense = new Expense();
        expense.setId(1);
        expense.setAmount(10.00);
        expense.setDescription(input);
        expense.setDate("2025-01-01");

        // User (THIS was missing)
        User user = new User();
        user.setUsername("testuser");

        // Approval (if accessed, keep non-null)
        Approval approval = new Approval();
        approval.setStatus("APPROVED");

        ExpenseWithUser e1 = new ExpenseWithUser(expense, user, approval);

        // Act
        String csv = expenseService.generateCsvReport(List.of(e1));

        // Assert – value should NOT be escaped
        assertTrue(csv.contains(expected));
    }




}