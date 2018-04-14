<%@ page import="java.util.Vector" %>
<%@ page import="org.inspirecenter.indoorpositioningsystem.model.Parameter" %>
<%@ page import="org.inspirecenter.indoorpositioningsystem.model.Dataset" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 03-Apr-18
  Time: 4:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CAIPS</title>
</head>
<body style="background-color: #fee;">

<h1>Context Aware Indoor Positioning System</h1>
<h2>Admin Page</h2>

<hr/>
<h3>Parameters</h3>

<table border="1">
    <tr>
        <td style="font-style: oblique;">Name</td>
        <td style="font-style: oblique;">Value</td>
        <td style="font-style: oblique;">Created by</td>
        <td style="font-style: oblique;">Created on</td>
        <td style="font-style: oblique;">Delete</td>
    </tr>
    <%
//        ofy().save().entity(new Parameter("hello", "world")).now();
        final List<Parameter> parameters = ofy().load().type(Parameter.class).list();
//final List<Parameter> parameters = new Vector<>(); ((Vector<Parameter>) parameters).addElement(new Parameter("hello", "world", "nearchos", System.currentTimeMillis()));//todo
        for(final Parameter parameter : parameters) {
            final String name = parameter.getName();
            final String value = parameter.getValue();
            final String createdBy = parameter.getCreatedBy();
            final String createdOn = new Date(parameter.getCreatedOn()).toString();
    %>
    <tr>
        <td><%=name%></td>
        <td><%=value%></td>
        <td><%=createdBy%></td>
        <td><%=createdOn%></td>
        <td><a href="delete-parameter?name=<%=name%>&redirect=/admin">Delete</a></td>
    </tr>
    <%
        }
    %>
</table>

<h4>Add new parameter</h4>
<form action="add-parameter" method="post" onsubmit="submitButton.disabled = true; return true;">
    <p>Name: <input type="text" name="name" title="Name"></p>
    <p>Value: <input type="text" name="value" title="Value"></p>
    <input type="hidden" name="redirect" value="/admin">
    <input type="submit" name="submitButton" value="Add parameter">
</form>

</body>
</html>
