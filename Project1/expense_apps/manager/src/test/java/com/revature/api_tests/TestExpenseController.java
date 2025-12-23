package com.revature.api_tests;

import com.revature.api.AuthenticationMiddleware;
import com.revature.api.ExpenseController;
import com.revature.repository.ExpenseWithUser;
import com.revature.repository.User;
import com.revature.service.ExpenseService;
import groovy.transform.Internal;
import io.javalin.http.Context;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.InternalServerErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.javalin.validation.Validator;
import java.net.ContentHandler;
import java.sql.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestExpenseController {
    @Mock
    private ExpenseService expenseService;
    @Mock
    private Context ctx;
    @Mock
    private Validator<Integer> intValidator;
    @InjectMocks
    private ExpenseController expenseController;



    @DisplayName("Test get pending expenses, positive test")
    @Test
    public void testGetPendingExpenses_Positive(){
        //Arrange
        ExpenseWithUser r1 = new ExpenseWithUser();
        ExpenseWithUser r2 = new ExpenseWithUser();
        ExpenseWithUser r3 = new ExpenseWithUser();
        List<ExpenseWithUser> pendingExpenses = new ArrayList<ExpenseWithUser>(Arrays.asList(r1, r2, r3));
        when(expenseService.getPendingExpenses()).thenReturn(pendingExpenses);
        //Act
        expenseController.getPendingExpenses(ctx);
        //Assert
        verify(expenseService, times(1)).getPendingExpenses();
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "data", pendingExpenses,
                "count", pendingExpenses.size()
        ));
    }

    @DisplayName("Test get pending expense, empty list from service layer")
    @Test
    public void testGetPendingExpenses_Empty(){
        //Arrange
        List<ExpenseWithUser> pendingExpenses = new ArrayList<ExpenseWithUser>();
        when(expenseService.getPendingExpenses()).thenReturn(pendingExpenses);
        //Act
        expenseController.getPendingExpenses(ctx);
        //Assert
        verify(expenseService, times(1)).getPendingExpenses();
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "data", pendingExpenses,
                "count", pendingExpenses.size()
        ));
    }

    @DisplayName("Test get pending expenses, null response from service layer")
    @Test
    public void testGetPendingExpense_Error(){
        //Arrange
        when(expenseService.getPendingExpenses()).thenReturn(null);
        //Act/Assert
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.getPendingExpenses(ctx));
        assertTrue(ex.getMessage().contains("Failed to retrieve pending expenses: "));
        verify(expenseService, times(1)).getPendingExpenses();
    }

    @DisplayName("Test approve expense, positive test with comment")
    @Test
    public void testApproveExpense_PositiveWithComment(){
        try(MockedStatic<AuthenticationMiddleware> mockedStatic = Mockito.mockStatic(AuthenticationMiddleware.class)) {
            //Arrange
            int expenseId = 3;
            when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
            when(intValidator.get()).thenReturn(expenseId);
            User manager = new User(1, "manager1", "password123", "Manager");
            when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
            // for comment
            Map<String, Object> exampleBody = new HashMap<String, Object>();
            exampleBody.put("comment", "example comment");
            when(ctx.bodyAsClass(Map.class)).thenReturn(exampleBody);
            when(expenseService.approveExpense(expenseId, manager.getId(), (String) exampleBody.get("comment"))).thenReturn(true);
            //Act
            expenseController.approveExpense(ctx);
            //Assert
            mockedStatic.verify(()->AuthenticationMiddleware.getAuthenticatedManager(ctx), times(1));
        }
        //Assert
        verify(ctx, times(1)).pathParamAsClass("expenseId", Integer.class);
        verify(ctx, times(1)).bodyAsClass(Map.class);
        verify(expenseService, times(1)).approveExpense(3, 1, "example comment");
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "message", "Expense approved successfully"
        ));
    }

    @DisplayName("Test approve expense, positive test with no comment")
    @Test
    public void testApproveExpense_PositiveNoComment(){
        try(MockedStatic<AuthenticationMiddleware> mockedStatic = Mockito.mockStatic(AuthenticationMiddleware.class)) {
            //Arrange
            int expenseId = 3;
            when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
            when(intValidator.get()).thenReturn(expenseId);
            User manager = new User(1, "manager1", "password123", "Manager");
            when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
            // for comment
            when(ctx.bodyAsClass(Map.class)).thenThrow(BadRequestResponse.class);
            when(expenseService.approveExpense(expenseId, manager.getId(), null)).thenReturn(true);
            //Act
            expenseController.approveExpense(ctx);
            //Assert
            mockedStatic.verify(()->AuthenticationMiddleware.getAuthenticatedManager(ctx), times(1));
        }
        //Assert
        verify(ctx, times(1)).pathParamAsClass("expenseId", Integer.class);
        verify(ctx, times(1)).bodyAsClass(Map.class);
        verify(expenseService, times(1)).approveExpense(3, 1, null);
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "message", "Expense approved successfully"
        ));
    }

    @DisplayName("Test approve expense, expense not found")
    @Test
    public void testApproveExpense_ExpenseNotFound(){
        //Arrange
        int expenseId = 123456789;
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(expenseId);
        User manager = new User();
        when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
        when(expenseService.approveExpense(expenseId, manager.getId(), null)).thenReturn(false);
        //Act
        NotFoundResponse ex = assertThrows(NotFoundResponse.class, ()->expenseController.approveExpense(ctx));
        //Assert
        assertEquals("Expense not found or could not be approved", ex.getMessage());

    }

    @DisplayName("Test approve expense, number format exception for expense id")
    @Test
    public void testApproveExpense_NumberFormatException(){
        //Arrange
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenThrow(NumberFormatException.class);
        //Act/Assert
        BadRequestResponse ex = assertThrows(BadRequestResponse.class, ()->expenseController.approveExpense(ctx));
        assertEquals("Invalid expense ID format", ex.getMessage());
    }

    @DisplayName("Test approve expense, get authenticated manager returns null value")
    @Test
    public void testApproveExpense_GetAuthenticatedManagerFailure(){
        //Arrange
        int expenseId = 3;
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(expenseId);
        when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(null);
        //Act/Arrange
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.approveExpense(ctx));
        assertTrue(ex.getMessage().contains("Failed to approve expense: "));
    }

    @DisplayName("Test approve expense, error during ctx.json")
    @Test
    public void testApproveExpense_jsonError(){
        //Arrange
        int expenseId = 3;
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(expenseId);
        User manager = new User(1, "manager1", "password123", "Manager");
        when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
        when(expenseService.approveExpense(expenseId, manager.getId(), null)).thenReturn(true);
        when(ctx.json(any())).thenThrow(InternalServerErrorResponse.class);
        //Act/Assert
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.approveExpense(ctx));
        assertTrue(ex.getMessage().contains("Failed to approve expense: "));
    }

    @DisplayName("Test deny expense, positive test, with comment")
    @Test
    public void testDenyExpense_PositiveWithComment(){
        try(MockedStatic<AuthenticationMiddleware> mockedStatic = Mockito.mockStatic(AuthenticationMiddleware.class)) {
            //Arrange
            int expenseId = 3;
            when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
            when(intValidator.get()).thenReturn(expenseId);
            User manager = new User(1, "manager1", "password123", "Manager");
            when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
            // for comment
            Map<String, Object> exampleBody = new HashMap<String, Object>();
            exampleBody.put("comment", "example comment");
            when(ctx.bodyAsClass(Map.class)).thenReturn(exampleBody);
            when(expenseService.denyExpense(expenseId, manager.getId(), (String) exampleBody.get("comment"))).thenReturn(true);
            //Act
            expenseController.denyExpense(ctx);
            //Assert
            mockedStatic.verify(()->AuthenticationMiddleware.getAuthenticatedManager(ctx), times(1));
        }
        //Assert
        verify(ctx, times(1)).pathParamAsClass("expenseId", Integer.class);
        verify(ctx, times(1)).bodyAsClass(Map.class);
        verify(expenseService, times(1)).denyExpense(3, 1, "example comment");
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "message", "Expense denied successfully"
        ));
        System.out.println("assertions done");
    }

    @DisplayName("Test deny expense, positive test, with no comment")
    @Test
    public void testDenyExpense_PositiveNoComment(){
        try(MockedStatic<AuthenticationMiddleware> mockedStatic = Mockito.mockStatic(AuthenticationMiddleware.class)){
            //Arrange
            int expenseId = 3;
            when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
            when(intValidator.get()).thenReturn(expenseId);
            User manager = new User(1, "manager1", "password123", "Manager");
            when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
            when(expenseService.denyExpense(expenseId, manager.getId(), null)).thenReturn(true);
            //Act
            expenseController.denyExpense(ctx);
            //Assert
            mockedStatic.verify(()->AuthenticationMiddleware.getAuthenticatedManager(ctx), times(1));
        }
        //Assert
        verify(ctx, times(1)).pathParamAsClass("expenseId", Integer.class);
        verify(expenseService, times(1)).denyExpense(3, 1, null);
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "message", "Expense denied successfully"
        ));
    }

    @DisplayName("Test deny expense, expense not found")
    @Test
    public void testDenyExpense_ExpenseNotFound(){
        //Arrange
        int expenseId = 123456789;
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(expenseId);
        User manager = new User();
        when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
        when(expenseService.denyExpense(expenseId, manager.getId(), null)).thenReturn(false);
        //Act
        NotFoundResponse ex = assertThrows(NotFoundResponse.class, ()->expenseController.denyExpense(ctx));
        //Assert
        assertEquals("Expense not found or could not be denied", ex.getMessage());
    }

    @DisplayName("Test deny expense, number format exception for expense id")
    @Test
    public void testDenyExpense_NumberFormatException(){
        //Arrange
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenThrow(NumberFormatException.class);
        //Act/Assert
        BadRequestResponse ex = assertThrows(BadRequestResponse.class, ()->expenseController.denyExpense(ctx));
        assertEquals("Invalid expense ID format", ex.getMessage());
    }

    @DisplayName("Test deny expense, get authenticated manager returns null value")
    @Test
    public void testDenyExpense_GetAuthenticatedManagerFailure(){
        //Arrange
        int expenseId = 3;
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(expenseId);
        when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(null);
        //Act/Arrange
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.denyExpense(ctx));
        assertTrue(ex.getMessage().contains("Failed to deny expense: "));
    }

    @DisplayName("Test deny expense, error during ctx.json")
    @Test
    public void testDenyExpense_jsonError(){
        //Arrange
        int expenseId = 3;
        when(ctx.pathParamAsClass("expenseId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(expenseId);
        User manager = new User(1, "manager1", "password123", "Manager");
        when(AuthenticationMiddleware.getAuthenticatedManager(ctx)).thenReturn(manager);
        when(expenseService.denyExpense(expenseId, manager.getId(), null)).thenReturn(true);
        when(ctx.json(any())).thenThrow(InternalServerErrorResponse.class);
        //Act/Assert
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.denyExpense(ctx));
        assertTrue(ex.getMessage().contains("Failed to deny expense: "));
    }

    @DisplayName("Test get all expenses, positive test")
    @Test
    public void testGetAllExpenses_Positive(){
        //Arrange
        ExpenseWithUser r1 = new ExpenseWithUser();
        ExpenseWithUser r2 = new ExpenseWithUser();
        ExpenseWithUser r3 = new ExpenseWithUser();
        List<ExpenseWithUser> expenses = new ArrayList<ExpenseWithUser>(Arrays.asList(r1, r2, r3));
        when(expenseService.getAllExpenses()).thenReturn(expenses);
        //Act
        expenseController.getAllExpenses(ctx);
        //Assert
        verify(expenseService, times(1)).getAllExpenses();
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "data", expenses,
                "count", expenses.size()
        ));
    }

    @DisplayName("Test get all expense, empty list from service layer")
    @Test
    public void testGetAllExpenses_Empty(){
        //Arrange
        List<ExpenseWithUser> expenses = new ArrayList<ExpenseWithUser>();
        when(expenseService.getAllExpenses()).thenReturn(expenses);
        //Act
        expenseController.getAllExpenses(ctx);
        //Assert
        verify(expenseService, times(1)).getAllExpenses();
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "data", expenses,
                "count", expenses.size()
        ));
    }

    @DisplayName("Test get all expenses, null response from service layer")
    @Test
    public void testGetAllExpenses_Error(){
        //Arrange
        when(expenseService.getAllExpenses()).thenReturn(null);
        //Act/Assert
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.getAllExpenses(ctx));
        assertTrue(ex.getMessage().contains("Failed to retrieve expenses: "));
        verify(expenseService, times(1)).getAllExpenses();
    }

    @DisplayName("Test get expenses by employee, positive test")
    @Test
    public void testGetExpensesByEmployee_Positive(){
        //Arrange
        int employeeId = 3;
        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(employeeId);

        ExpenseWithUser r1 = new ExpenseWithUser();
        ExpenseWithUser r2 = new ExpenseWithUser();
        ExpenseWithUser r3 = new ExpenseWithUser();
        List<ExpenseWithUser> expenses = new ArrayList<ExpenseWithUser>(Arrays.asList(r1, r2, r3));
        when(expenseService.getExpensesByEmployee(employeeId)).thenReturn(expenses);
        //Act
        expenseController.getExpensesByEmployee(ctx);
        //Assert
        verify(expenseService, times(1)).getExpensesByEmployee(employeeId);
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "data", expenses,
                "count", expenses.size(),
                "employeeId", employeeId
        ));
    }

    @DisplayName("Test get expenses by employee, empty list from service layer")
    @Test
    public void testGetExpensesByEmployee_Empty(){
        //Arrange
        int employeeId = 3;
        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(employeeId);

        List<ExpenseWithUser> expenses = new ArrayList<ExpenseWithUser>();
        when(expenseService.getExpensesByEmployee(employeeId)).thenReturn(expenses);
        //Act
        expenseController.getExpensesByEmployee(ctx);
        //Assert
        verify(expenseService, times(1)).getExpensesByEmployee(employeeId);
        verify(ctx, times(1)).json(Map.of(
                "success", true,
                "data", expenses,
                "count", expenses.size(),
                "employeeId", employeeId
        ));
    }

    @DisplayName("Test get expenses by employee, null response from service layer")
    @Test
    public void testGetExpensesByEmployee_ServiceError(){
        //Arrange
        int employeeId = 3;
        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenReturn(employeeId);
        when(expenseService.getExpensesByEmployee(employeeId)).thenReturn(null);
        //Act/Assert
        InternalServerErrorResponse ex = assertThrows(InternalServerErrorResponse.class, ()->expenseController.getExpensesByEmployee(ctx));
        assertTrue(ex.getMessage().contains("Failed to retrieve expenses for employee: "));
        verify(expenseService, times(1)).getExpensesByEmployee(employeeId);
    }

    @DisplayName("Test get expenses by employee, number format exception error")
    @Test
    public void testGetExpensesByEmployee_NumberFormatException(){
        //Arrange
        when(ctx.pathParamAsClass("employeeId", Integer.class)).thenReturn(intValidator);
        when(intValidator.get()).thenThrow(NumberFormatException.class);
        //Act/Assert
        BadRequestResponse ex = assertThrows(BadRequestResponse.class, ()->expenseController.getExpensesByEmployee(ctx));
        assertEquals("Invalid employee ID format", ex.getMessage());
    }
}
