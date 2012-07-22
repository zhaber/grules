<%@ page import="grules.User" %>


<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'login', 'error')} ">
	<label for="login">
		<g:message code="user.login.label" default="Login" />
		
	</label>
	<g:textField name="login" value="${userInstance?.login}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} ">
	<label for="password">
		<g:message code="user.password.label" default="Password" />
		
	</label>
	<g:textField name="password" value="${userInstance?.password}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'age', 'error')} required">
	<label for="age">
		<g:message code="user.age.label" default="Age" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="age" required="" value="${fieldValue(bean: userInstance, field: 'age')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'beginDate', 'error')} required">
	<label for="beginDate">
		<g:message code="user.beginDate.label" default="Begin Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="beginDate" precision="day"  value="${userInstance?.beginDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'endDate', 'error')} required">
	<label for="endDate">
		<g:message code="user.endDate.label" default="End Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="endDate" precision="day"  value="${userInstance?.endDate}"  />
</div>