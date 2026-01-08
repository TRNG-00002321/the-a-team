package com.revature.unit_tests.api_tests;

import com.revature.api.ReportController;
import com.revature.repository.ExpenseWithUser;
import com.revature.service.ExpenseService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestReportController {
    @Mock
    private ExpenseService expenseService;

    @Mock
    private Context ctx;

    @InjectMocks
    private ReportController reportController;

    // MU-142
    @DisplayName("Test generateAllExpenseReport Happy Path")
    @Test
    public void testGenerateAllExpensesReport_positive() {
        // Arrange
        List<ExpenseWithUser> expenses = List.of(mock(ExpenseWithUser.class));
        String csvContent = "Expense ID, Employee, Description\n1, Joe, Test Data";

        when(expenseService.getAllExpenses()).thenReturn(expenses);
        when(expenseService.generateCsvReport(expenses)).thenReturn(csvContent);

        // Act
        reportController.generateAllExpensesReport(ctx);

        // Assert
        verify(expenseService).getAllExpenses();
        verify(expenseService).generateCsvReport(expenses);

        verify(ctx).contentType("text/csv");
        verify(ctx).header("Content-Disposition", "attachment; filename=\"all_expenses_report.csv\"");
        verify(ctx).result(csvContent);
    }

    // MU-143
    @DisplayName("Test generateAllExpensesReport Sad Path")
    @Test
    public void testGenerateAllExpenseReport_negative() {
        // Arrange
        when(expenseService.getAllExpenses()).thenThrow(new RuntimeException());

        // Act

        // Assert
        assertThrows(InternalServerErrorResponse.class, () -> reportController.generateAllExpensesReport(ctx));
    }

    // MU-144
    @DisplayName("Test generateEmployeeExpensesReport Happy Path")
    @Test
    public void testGenerateEmployeeExpensesReport_positive() {
        // Arrange
        List<ExpenseWithUser> expenses = List.of(mock(ExpenseWithUser.class));
        String csvContent = "Expense ID, Employee, Description\n1, Joe, Test Data";
        Validator<Integer> validator = mock(Validator.class);

        when(expenseService.getExpensesByEmployee(1)).thenReturn(expenses);
        when(expenseService.generateCsvReport(expenses)).thenReturn(csvContent);
        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(validator);
        when(validator.get()).thenReturn(1);

        // Act
        reportController.generateEmployeeExpensesReport(ctx);

        // Assert
        verify(ctx).contentType("text/csv");
        verify(ctx).header("Content-Disposition", "attachment; filename=\"employee_1_expenses_report.csv\"");
        verify(ctx).result(csvContent);
    }

    // MU-145
    @DisplayName("Test generateEmployeeExpensesReport Sad Path - BadRequestResponse")
    @Test
    public void testGenerateEmployeeExpensesReport_badRequest() {
        // Arrange
        Validator<Integer> validator = mock(Validator.class);

        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(validator);
        when(validator.get()).thenReturn(1);
        when(expenseService.getExpensesByEmployee(1)).thenThrow(new NumberFormatException());

        // Act

        // Assert
        Exception exception = assertThrows(BadRequestResponse.class, () ->  reportController.generateEmployeeExpensesReport(ctx));
        assertEquals("Invalid employee ID format", exception.getMessage());
    }

    // MU-146
    @DisplayName("Test generateEmployeeExpensesReport Sad Path - InternalServerErrorResponse")
    @Test
    public void testGenerateEmployeeExpensesReport_internalServerError() {
        // Arrange
        Validator<Integer> validator = mock(Validator.class);

        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(validator);
        when(validator.get()).thenReturn(1);
        when(expenseService.getExpensesByEmployee(1)).thenThrow(new RuntimeException());

        // Act

        // Assert
        Exception exception = assertThrows(InternalServerErrorResponse.class, () ->  reportController.generateEmployeeExpensesReport(ctx));
        assertTrue(exception.getMessage().contains("Failed to generate employee expenses report"));
    }

    // MU-147
    @DisplayName("Test generateCategoryExpensesReport Happy Path")
    @Test
    public void testGenerateCategoryExpensesReport_positive() {
        // Arrange
        List<ExpenseWithUser> expenses = List.of(mock(ExpenseWithUser.class));
        String csvContent = "Expense ID, Employee, Description\n1, Joe, Test Data";

        when(expenseService.getExpensesByCategory("Some Category")).thenReturn(expenses);
        when(expenseService.generateCsvReport(expenses)).thenReturn(csvContent);
        when(ctx.pathParam("category")).thenReturn("Some Category");
        String safeCategory = "Some Category".replaceAll("[^a-zA-Z0-9_-]", "_");

        // Act
        reportController.generateCategoryExpensesReport(ctx);

        // Assert
        verify(ctx).contentType("text/csv");
        verify(ctx).header("Content-Disposition", "attachment; filename=\"category_" + safeCategory + "_expenses_report.csv\"");
        verify(ctx).result(csvContent);
    }

    // MU-148
    @DisplayName("Test generateCategoryExpensesReport Sad Path - BadRequestResponse")
    @Test
    public void testGenerateCategoryExpensesReport_badRequest() {
        // Arrange
        when(ctx.pathParam("category")).thenReturn(null);

        // Act

        // Assert
        Exception exception = assertThrows(BadRequestResponse.class, () ->  reportController.generateCategoryExpensesReport(ctx));
        assertEquals("Category parameter is required", exception.getMessage());
    }

    // MU-149
    @DisplayName("Test generateCategoryExpensesReport Sad Path - InternalServerErrorResponse")
    @Test
    public void testGenerateCategoryExpensesReport_internalServerError() {
        // Arrange
        when(ctx.pathParam("category")).thenThrow(new RuntimeException());

        // Act

        // Assert
        Exception exception = assertThrows(InternalServerErrorResponse.class, () -> reportController.generateCategoryExpensesReport(ctx));
        assertTrue(exception.getMessage().contains("Failed to generate category expenses report"));
    }

    // MU-150
    @DisplayName("Test generateDateRangeExpensesReport Happy Path")
    @Test
    public void testGenerateDateRangeExpensesReport_positive() {
        // Arrange
        List<ExpenseWithUser> expenses = List.of(mock(ExpenseWithUser.class));
        String csvContent = "Expense ID, Employee, Description\n1, Joe, Test Data";

        when(expenseService.getExpensesByDateRange("2025-12-23", "2025-12-25")).thenReturn(expenses);
        when(expenseService.generateCsvReport(expenses)).thenReturn(csvContent);

        when(ctx.queryParam("startDate")).thenReturn("2025-12-23");
        when(ctx.queryParam("endDate")).thenReturn("2025-12-25");

        // Act
        reportController.generateDateRangeExpensesReport(ctx);

        // Assert
        verify(ctx).contentType("text/csv");
        verify(ctx).header("Content-Disposition", "attachment; filename=\"expenses_2025-12-23_to_2025-12-25_report.csv\"");
        verify(ctx).result(csvContent);
    }

    // MU-151
    @DisplayName("Test generateDateRangeExpensesReport Sad Path - BadRequestResponse Due to Null Values")
    @Test
    public void testGenerateDateRangeExpensesReport_badRequest_nullVals() {
        // Arrange
        when(ctx.queryParam("startDate")).thenReturn(null);
        when(ctx.queryParam("endDate")).thenReturn("2025-12-25");

        // Act

        // Assert
        Exception exception = assertThrows(BadRequestResponse.class, () -> reportController.generateDateRangeExpensesReport(ctx));
        assertEquals("Both startDate and endDate query parameters are required (format: YYYY-MM-DD)",  exception.getMessage());
    }

    // MU-152
    @DisplayName("Test generateDateRangeExpensesReport Sad Path - BadRequestResponse Due to Invalid Date Format")
    @Test
    public void testGenerateDateRangeExpensesReport_badRequest_invalidFormat() {
        // Arrange
        when(ctx.queryParam("startDate")).thenReturn("12-23-2025");
        when(ctx.queryParam("endDate")).thenReturn("12-25-2025");

        // Act

        // Assert
        Exception exception = assertThrows(BadRequestResponse.class, () -> reportController.generateDateRangeExpensesReport(ctx));
        assertEquals("Invalid date format. Use YYYY-MM-DD format", exception.getMessage());
    }

    // MU-153
    @DisplayName("Test generateDateRangeExpensesReport Sad Path - InternalServerErrorResponse")
    @Test
    public void testGenerateDateRangeExpensesReport_internalServerError() {
        // Arrange
        when(ctx.queryParam("startDate")).thenThrow(new RuntimeException());

        // Act

        // Assert
        Exception exception = assertThrows(InternalServerErrorResponse.class, () -> reportController.generateDateRangeExpensesReport(ctx));
        assertTrue(exception.getMessage().contains("Failed to generate date range expenses report"));
    }

    // MU-154
    @DisplayName("Test generatePendingExpensesReport Happy Path")
    @Test
    public void testGeneratePendingExpensesReport_positive() {
        // Arrange
        List<ExpenseWithUser> expenses = List.of(mock(ExpenseWithUser.class));
        String csvContent = "Expense ID, Employee, Description\n1, Joe, Test Data";

        when(expenseService.getPendingExpenses()).thenReturn(expenses);
        when(expenseService.generateCsvReport(expenses)).thenReturn(csvContent);

        // Act
        reportController.generatePendingExpensesReport(ctx);

        // Assert
        verify(ctx).contentType("text/csv");
        verify(ctx).header("Content-Disposition", "attachment; filename=\"pending_expenses_report.csv\"");
        verify(ctx).result(csvContent);
    }

    // MU-155
    @DisplayName("Test generatePendingExpensesReport Sad Path")
    @Test
    public void testGeneratePendingExpensesReport_negative() {
        // Arrange
        when(expenseService.getPendingExpenses()).thenThrow(new RuntimeException());

        // Act

        // Assert
        Exception exception = assertThrows(InternalServerErrorResponse.class, () -> reportController.generatePendingExpensesReport(ctx));
        assertTrue(exception.getMessage().contains("Failed to generate pending expenses report"));
    }
}
