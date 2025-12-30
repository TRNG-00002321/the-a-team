from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then


@given(u'the application is running')
def step_impl(context):
    raise StepNotImplementedError(u'Given the application is running')


@given(u'the test database is already seeded with users')
def step_impl(context):
    raise StepNotImplementedError(u'Given the test database is already seeded with users')


@given(u'the employee is on the login screen')
def step_impl(context):
    raise StepNotImplementedError(u'Given the employee is on the login screen')


@when(u'the employee enters username "employee1"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters username "employee1"')


@when(u'the employee enters password "password123"')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters password "password123"')


@when(u'the employee clicks the login button')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks the login button')


@then(u'the employee sees the message: "Login successful! Redirecting to employee dashboard..."')
def step_impl(context):
    raise StepNotImplementedError(
        u'Then the employee sees the message: "Login successful! Redirecting to employee dashboard..."')


@then(u'the employee is redirected to the employee dashboard')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee is redirected to the employee dashboard')


@when(u'the employee enters username wronguser')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters username wronguser')


@when(u'the employee enters password password123')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters password password123')


@then(u'the employee is not redirected to the dashboard')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee is not redirected to the dashboard')


@then(u'the employee sees the message: Invalid Credentials')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee sees the message: Invalid Credentials')


@when(u'the employee enters username employee1')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters username employee1')


@when(u'the employee enters password wrongpassword')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters password wrongpassword')


@when(u'the employee enters username manager1')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee enters username manager1')


@then(u'the employee sees the message: Login failed')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee sees the message: Login failed')


@when(u'the employee does not input any value for username')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee does not input any value for username')


@then(u'the username field is selected')
def step_impl(context):
    raise StepNotImplementedError(u'Then the username field is selected')


@given(u'the employee enters username "employee1"')
def step_impl(context):
    raise StepNotImplementedError(u'Given the employee enters username "employee1"')


@when(u'the employee does not input any value for the password')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee does not input any value for the password')


@then(u'the password field is selected')
def step_impl(context):
    raise StepNotImplementedError(u'Then the password field is selected')


@given(u'the employee is logged in')
def step_impl(context):
    raise StepNotImplementedError(u'Given the employee is logged in')


@when(u'the employee clicks the logout button')
def step_impl(context):
    raise StepNotImplementedError(u'When the employee clicks the logout button')


@then(u'the employee is redirected to the login page')
def step_impl(context):
    raise StepNotImplementedError(u'Then the employee is redirected to the login page')