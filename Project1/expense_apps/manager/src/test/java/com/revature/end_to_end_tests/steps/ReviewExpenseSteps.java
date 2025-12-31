package com.revature.end_to_end_tests.steps;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReviewExpenseSteps {
    @Given("the manager is on the pending expenses screen")
    public void theManagerIsOnThePendingExpensesScreen() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("an expense with id: {string}, date: {string}, amount: {string}, and description: {string} is pending")
    public void anExpenseWithIdDateAmountAndDescriptionIsPending(String arg0, String arg1, String arg2, String arg3) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("the manager clicks the review button for an expense with id: {string}, date: {string}, amount: {string}, and description: {string}")
    public void theManagerClicksTheReviewButtonForAnExpenseWithIdDateAmountAndDescription(String arg0, String arg1, String arg2, String arg3) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("the manager inputs in the comment box: {string}")
    public void theManagerInputsInTheCommentBox(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("the manager clicks the {string} button")
    public void theManagerClicksTheButton(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the manager sees a message containing {string}")
    public void theManagerSeesAMessageContaining(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("the manager clicks the all expenses button")
    public void theManagerClicksTheAllExpensesButton() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("an expense with id: {string}, date: {string}, amount: {string}, and description: {string} is shown as {string}, with comment {string}")
    public void anExpenseWithIdDateAmountAndDescriptionIsShownAsWithComment(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("the manager cancels the review")
    public void theManagerCancelsTheReview() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the manager should stay on the pending expenses screen")
    public void theManagerShouldStayOnThePendingExpensesScreen() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
