from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then

@when(u'the filter by status option is selected for all')
def step_impl(context):
    raise StepNotImplementedError(u'When the filter by status option is selected for all')


@then(u'all expenses are shown')
def step_impl(context):
    raise StepNotImplementedError(u'Then all expenses are shown')


@when(u'the filter by status option Pending is selected')
def step_impl(context):
    raise StepNotImplementedError(u'When the filter by status option Pending is selected')


@then(u'all expenses only with status: Pending are shown')
def step_impl(context):
    raise StepNotImplementedError(u'Then all expenses only with status: Pending are shown')


@when(u'the filter by status option Approved is selected')
def step_impl(context):
    raise StepNotImplementedError(u'When the filter by status option Approved is selected')


@then(u'all expenses only with status: Approved are shown')
def step_impl(context):
    raise StepNotImplementedError(u'Then all expenses only with status: Approved are shown')


@when(u'the filter by status option Denied is selected')
def step_impl(context):
    raise StepNotImplementedError(u'When the filter by status option Denied is selected')


@then(u'all expenses only with status: Denied are shown')
def step_impl(context):
    raise StepNotImplementedError(u'Then all expenses only with status: Denied are shown')