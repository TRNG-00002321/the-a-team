package com.revature.end_to_end_tests.steps;

import com.revature.end_to_end_tests.context.TestContext;
import com.revature.end_to_end_tests.pages.DashboardPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewExpenseSteps {
    TestContext context;
    DashboardPage dashboardPage;

    public ReviewExpenseSteps(TestContext context){
        this.context = context;
    }

    @Given("the manager is on the pending expenses screen")
    public void theManagerIsOnThePendingExpensesScreen() {
        dashboardPage = context.dashboardPage();
        dashboardPage.goToPendingExpensesScreen();
        //find h3 of the web element where id = 'pending-expenses-section'
        WebElement div = dashboardPage.waitForElement(By.id("pending-expenses-section"));
        WebElement header3 = div.findElement(By.tagName("h3"));
        assertEquals("Pending Expenses for Review", header3.getText());
    }

    @And("an expense with id: {string}, date: {string}, amount: {string}, and description: {string} is pending")
    public void anExpenseWithIdDateAmountAndDescriptionIsPending(String id, String date, String amount, String desc) {
        List<WebElement> rows = context.getDriver().findElements(By.cssSelector("#pending-expenses-list tr"));
        boolean found = false;

        //skip 1st row b/c it is the header
        for(int i = 1; i < rows.size(); i++){
            WebElement row = rows.get(i);
            String currentId = row.findElement(By.cssSelector("td:nth-child(1)")).getText();
            String currentDate = row.findElement(By.cssSelector("td:nth-child(2)")).getText();
            String currentAmount = row.findElement(By.cssSelector("td:nth-child(3)")).getText();
            String currentDesc = row.findElement(By.cssSelector("td:nth-child(4)")).getText();
            if(currentId.contains("ID: "+id) && currentDate.contains(date) && currentAmount.contains("$"+amount) && currentDesc.contains(desc)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @When("the manager clicks the review button for an expense with id: {string}, date: {string}, amount: {string}, and description: {string}")
    public void theManagerClicksTheReviewButtonForAnExpenseWithIdDateAmountAndDescription(String id, String date, String amount, String desc) {
        List<WebElement> rows = context.getDriver().findElements(By.cssSelector("#pending-expenses-list tr"));
        WebElement specificRow = null;

        //skip 1st row b/c it is the header
        for(int i = 1; i < rows.size(); i++){
            WebElement row = rows.get(i);
            String currentId = row.findElement(By.cssSelector("td:nth-child(1)")).getText();
            String currentDate = row.findElement(By.cssSelector("td:nth-child(2)")).getText();
            String currentAmount = row.findElement(By.cssSelector("td:nth-child(3)")).getText();
            String currentDesc = row.findElement(By.cssSelector("td:nth-child(4)")).getText();
            if(currentId.contains("ID: "+id) && currentDate.contains(date) && currentAmount.contains("$"+amount) && currentDesc.contains(desc)) {
                specificRow = rows.get(i);
                break;
            }
        }
        WebElement reviewButton = specificRow.findElement(By.cssSelector("button"));
        reviewButton.click();
    }

    @And("the manager inputs in the comment box: {string}")
    public void theManagerInputsInTheCommentBox(String arg0) {
        dashboardPage = context.dashboardPage();
        WebElement commentBox = dashboardPage.waitForClickable(By.cssSelector("#review-comment"));
        commentBox.sendKeys(arg0);
    }

    @And("the manager clicks the {string} button")
    public void theManagerClicksTheButton(String arg0) {
        dashboardPage = context.dashboardPage();
        String buttonId = arg0.toLowerCase()+"-expense";
        WebElement button = dashboardPage.waitForElement(By.id(buttonId));
        button.click();
    }

    @Then("the manager sees a message containing {string}")
    public void theManagerSeesAMessageContaining(String arg0) {
        dashboardPage = context.dashboardPage();
        WebElement message = dashboardPage.waitForElement(By.cssSelector("#review-message p"));
        assertTrue(message.getText().contains(arg0));
    }

    @And("the manager clicks the all expenses button")
    public void theManagerClicksTheAllExpensesButton() {
        dashboardPage = context.dashboardPage();
        dashboardPage.click(By.id("cancel-review"));
        dashboardPage.goToAllExpensesScreen();
    }

    @And("an expense with id: {string}, date: {string}, amount: {string}, and description: {string} is shown as {string}, with comment {string}")
    public void anExpenseWithIdDateAmountAndDescriptionIsShownAsWithComment(String id, String date, String amount, String desc, String status, String comment) {
        String expectedStatus = null;
        String expectedComment = null;

        if(status.equals("Approve"))
            expectedStatus = "APPROVED";
        else if(status.equals("Deny"))
            expectedStatus = "DENIED";
        else
            fail("Status is not approve or deny");
        if(comment == null || comment.isEmpty())
            expectedComment = "-";
        else
            expectedComment = comment;

        List<WebElement> rows = context.getDriver().findElements(By.cssSelector("#all-expenses-list tr"));
        boolean found = false;
        //skip 1st row b/c it is the header
        for(int i = 1; i < rows.size(); i++){
            WebElement row = rows.get(i);
            String currentId = row.findElement(By.cssSelector("td:nth-child(1)")).getText();
            String currentDate = row.findElement(By.cssSelector("td:nth-child(2)")).getText();
            String currentAmount = row.findElement(By.cssSelector("td:nth-child(3)")).getText();
            String currentDesc = row.findElement(By.cssSelector("td:nth-child(4)")).getText();
            String currentStatus = row.findElement(By.cssSelector("td:nth-child(5)")).getText();
            String currentComment = row.findElement(By.cssSelector("td:nth-child(7)")).getText();
            if(currentId.contains("ID: "+id) && currentDate.contains(date) && currentAmount.contains("$"+amount) && currentDesc.contains(desc) && currentStatus.equals(expectedStatus) && currentComment.equals(expectedComment)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @And("the manager cancels the review")
    public void theManagerCancelsTheReview() {
        dashboardPage = context.dashboardPage();
        dashboardPage.click(By.id("cancel-review"));
    }

    @Then("the manager should stay on the pending expenses screen")
    public void theManagerShouldStayOnThePendingExpensesScreen() {
        dashboardPage = context.dashboardPage();
        //find h3 of the web element where id = 'pending-expenses-section'
        WebElement div = dashboardPage.waitForElement(By.id("pending-expenses-section"));
        WebElement header3 = div.findElement(By.tagName("h3"));
        assertEquals("Pending Expenses for Review", header3.getText());
    }
}
