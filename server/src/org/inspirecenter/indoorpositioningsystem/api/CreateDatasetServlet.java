package org.inspirecenter.indoorpositioningsystem.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class CreateDatasetServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        // todo check for magic

        Message message;

        final BufferedReader bufferedReader = request.getReader();
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        final String json = stringBuilder.toString();

        try {
            final Dataset dataset = gson.fromJson(json, Dataset.class);

            ofy().save().entity(dataset).now();

            // invalidate memcache
            memcacheService.delete(DatasetsServlet.MEM_CACHE_DATASETS);

            message = Message.createOkMessage("Dataset created with id: " + dataset.getId());
        } catch (JsonSyntaxException jse) {
            message = Message.createErrorMessage(jse.getMessage());
        }

        final String errorMessageAsJson = gson.toJson(message, Message.class);
        response.getWriter().println(errorMessageAsJson);
    }
}
