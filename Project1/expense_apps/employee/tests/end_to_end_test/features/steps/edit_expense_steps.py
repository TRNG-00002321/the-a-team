from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then

@when(u'the employee clicks the edit button for the expense with description "Hotel Stay"')
def step_impl(context):
    raise StepNotImplementedError(
        u'When the employee clicks the edit button for the expense with description "Hotel Stay"')


@when(u'the employee is redirected to the edit menu')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee is redirected to the edit menu')


@when(u'the employee inputs into the amount field: 123')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the amount field: 123')


@when(u'the employee inputs into the description field: example description')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the description field: example description')


@when(u'the employee inputs into the date field: 2025-12-30')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the date field: 2025-12-30')


@when(u'the employee clicks the update expense button')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks the update expense button')


@then(u'the employee sees the edit message: "Expense updated successfully!"')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee sees the message: "Expense updated successfully!"')


@then(u'the expense is updated with the given 123, example description, and 2025-12-30')
def step_impl(context):
    raise StepNotImplementedError(
        u'Then the expense is updated with the given 123, example description, and 2025-12-30')


@when(u'the employee inputs into the amount field: 999')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the amount field: 999')


@when(u'the employee inputs into the description field: fix door')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the description field: fix door')


@when(u'the employee inputs into the date field: 2025-10-10')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the date field: 2025-10-10')


@then(u'the expense is updated with the given 999, fix door, and 2025-10-10')
def step_impl(context):
    raise StepNotImplementedError(u'Then the expense is updated with the given 999, fix door, and 2025-10-10')


@when(u'the employee inputs into the amount field: "999999"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the amount field: "999999"')


@when(u'the employee inputs into the description field: "wont be updated"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the description field: "wont be updated"')


@when(u'the employee inputs into the date field: "2025-12-30"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs into the date field: "2025-12-30"')


@when(u'the employee clicks the cancel button')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks the cancel button')


@then(u'the expense with description "Hotel Stay" still exists')
def step_impl(context):
    raise StepNotImplementedError(u'Then the expense with description "Hotel Stay" still exists')


@given(u'an expense with the description: "Hotel Stay", amount: "150", and date: "2025-12-30"')
def step_impl(context):
    raise StepNotImplementedError(
        u'Given an expense with the description: "Hotel Stay", amount: "150", and date: "2025-12-30"')


@then(u'the expense is shown with the the amount: "150", description: "Hotel Stay", and the date: "2025-12-30"')
def step_impl(context):
    raise StepNotImplementedError(
        u'Then the expense is shown with the the amount: "150", description: "Hotel Stay", and the date: "2025-12-30"')