from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then

@given(u'the employee is at the submit expense menu')
def step_impl(context):
    raise StepNotImplementedError(u'Given the employee is at the submit expense menu')


@when(u'the employee inputs a new amount: 123')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new amount: 123')


@when(u'the employee inputs a new description: example description')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new description: example description')


@when(u'the employee inputs a new date: 2025-12-30')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new date: 2025-12-30')


@when(u'the employee clicks the submit expense button')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks the submit expense button')


@then(u'the employee sees the message: "Expense submitted successfully!"')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee sees the message: "Expense submitted successfully!"')


@then(u'the expense is shown with the the amount: 123, description: example description, and the date: 2025-12-30')
def step_impl(context):
    raise StepNotImplementedError(
        u'Then the expense is shown with the the amount: 123, description: example description, and the date: 2025-12-30')


@when(u'the employee inputs a new amount: 999')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new amount: 999')


@when(u'the employee inputs a new description: fix door')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new description: fix door')


@when(u'the employee inputs a new date: 2025-10-10')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new date: 2025-10-10')


@then(u'the expense is shown with the the amount: 999, description: fix door, and the date: 2025-10-10')
def step_impl(context):
    raise StepNotImplementedError(
        u'Then the expense is shown with the the amount: 999, description: fix door, and the date: 2025-10-10')


@when(u'the amount field is empty')
def step_impl(context):
    raise StepNotImplementedError(u'When the amount field is empty')


@then(u'the amount field is selected')
def step_impl(context):
    raise StepNotImplementedError(u'Then the amount field is selected')


@then(u'the employee stays on the submit menu screen')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee stays on the submit menu screen')


@when(u'the employee inputs a new amount: "125"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new amount: "125"')


@when(u'the description field is empty')
def step_impl(context):
    raise StepNotImplementedError(u'When the description field is empty')


@then(u'the description field is selected')
def step_impl(context):
    raise StepNotImplementedError(u'Then the description field is selected')


@when(u'the employee inputs a new amount: "100"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new amount: "100"')


@when(u'the employee inputs a new description: "today\'s date"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee inputs a new description: "today\'s date"')


@then(u'an expense with today\'s date, amount: "100" and description: "today\'s date" is shown')
def step_impl(context):
    raise StepNotImplementedError(
        u'Then an expense with today\'s date, amount: "100" and description: "today\'s date" is shown')