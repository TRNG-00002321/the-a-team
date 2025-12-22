package com.revature.api_tests;

import com.revature.api.AuthenticationMiddleware;
import com.revature.api.ExpenseController;
import com.revature.repository.ExpenseWithUser;
import com.revature.repository.User;
import com.revature.service.ExpenseService;
import io.javalin.http.Context;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.InternalServerErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        when(expenseService.approveExpense(expenseId, manager.getId(), (String)exampleBody.get("comment"))).thenReturn(true);
        //Act
        expenseController.approveExpense(ctx);
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

    }

    @DisplayName("Test approve expense, number format exception for expense id")
    @Test
    public void testApproveExpense_NumberFormatException(){

    }

    @DisplayName("Test approve expense, get authenticate manager returns null value")
    @Test
    public void testApproveExpense_GetAuthenticateManagerFailure(){

    }

    @DisplayName("Test approve expense, error during ctx.json")
    @Test
    public void testApproveExpense_jsonError(){

    }






}
