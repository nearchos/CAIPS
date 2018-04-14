package org.inspirecenter.indoorpositioningsystem.admin;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.api.DatasetServlet;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.Message;
import org.inspirecenter.indoorpositioningsystem.model.Parameter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class DeleteParameterServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String parameterName = request.getParameter("name");
        final String redirect = request.getParameter("redirect");

        Message message;

        if(parameterName == null || parameterName.isEmpty()) {
            message = Message.createErrorMessage("Invalid or missing parameter 'name': " + parameterName);
        } else {
            try {
                // delete from datastore
                ofy().delete().type(Parameter.class).id(parameterName).now();

                message = Message.createOkMessage("Parameter with name: '" + parameterName + "' deleted");
            } catch (NumberFormatException nfe) {
                message = Message.createErrorMessage(nfe.getMessage());
            }
        }

        final String messageAsJson = gson.toJson(message, Message.class);
        response.getWriter().println(messageAsJson);

        if(redirect != null) response.sendRedirect(redirect);
    }
}