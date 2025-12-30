from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then

@given(u'the employee is on my expenses')
def step_impl(context):
    raise StepNotImplementedError(u'Given the employee is on my expenses')


@given(u'an expense with the description: "Hotel Stay" is shown')
def step_impl(context):
    raise StepNotImplementedError(u'Given an expense with the description: "Hotel Stay" is shown')


@given(u'the expense with description "Hotel Stay" is pending')
def step_impl(context):
    raise StepNotImplementedError(u'Given the expense with description "Hotel Stay" is pending')


@when(u'the employee clicks the delete button for the expense with description "Hotel Stay"')
def step_impl(context):
    raise StepNotImplementedError(
        u'When the employee clicks the delete button for the expense with description "Hotel Stay"')


@when(u'the employee clicks ok for the confirmation alert')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks ok for the confirmation alert')


@when(u'the employee is shown another alert with message: "Expense deleted successfully!"')
def step_impl(context):
    raise StepNotImplementedError(
        u'When the employee is shown another alert with message: "Expense deleted successfully!"')


@when(u'the employee clicks ok to close the alert')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks ok to close the alert')


@then(u'the expense with description: "Hotel Stay" is no longer shown')
def step_impl(context):
    raise StepNotImplementedError(u'Then the expense with description: "Hotel Stay" is no longer shown')


@given(u'an expense with the description: "Travel Expenses" is shown')
def step_impl(context):
    raise StepNotImplementedError(u'Given an expense with the description: "Travel Expenses" is shown')


@given(u'the expense with description "Travel Expenses" is pending')
def step_impl(context):
    raise StepNotImplementedError(u'Given the expense with description "Travel Expenses" is pending')


@when(u'the employee clicks the delete button for the expense with description "Travel Expenses"')
def step_impl(context):
    raise StepNotImplementedError(
        u'When the employee clicks the delete button for the expense with description "Travel Expenses"')


@when(u'the employee clicks cancel for the confirmation alert')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks cancel for the confirmation alert')


@then(u'the expense with description: "Travel Expenses" is still shown')
def step_impl(context):
    raise StepNotImplementedError(u'Then the expense with description: "Travel Expenses" is still shown')