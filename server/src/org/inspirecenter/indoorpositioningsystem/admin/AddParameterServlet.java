package org.inspirecenter.indoorpositioningsystem.admin;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Message;
import org.inspirecenter.indoorpositioningsystem.model.Parameter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AddParameterServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String name = request.getParameter("name");
        final String value = request.getParameter("value");
        final String redirect = request.getParameter("redirect");

        Message message;

        if(name == null || name.isEmpty()) {
            message = Message.createErrorMessage("Invalid or missing 'name' parameter: " + name);
        } else if(value == null || value.isEmpty()) {
            message = Message.createErrorMessage("Invalid or missing 'value' parameter: " + value);
        } else {
            final User user = UserServiceFactory.getUserService().getCurrentUser();
            if(user == null) {
                message = Message.createErrorMessage("Invalid or no user logged in");
            } else {
                final Parameter parameter = new Parameter(name, value, user.getEmail(), System.currentTimeMillis());
                ofy().save().entity(parameter).now();

                message = Message.createOkMessage("Parameter created: " + parameter);
            }
        }

        final String messageAsJson = gson.toJson(message, Message.class);

        response.setContentType("text/html");
        response.getWriter().println(messageAsJson);

        if(redirect != null) response.sendRedirect(redirect);
    }
}
