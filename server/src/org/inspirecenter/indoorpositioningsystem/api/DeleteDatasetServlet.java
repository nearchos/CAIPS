package org.inspirecenter.indoorpositioningsystem.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class DeleteDatasetServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        // todo check for magic

        final String datasetId = request.getParameter("datasetId");

        Message message;

        if(datasetId == null || datasetId.isEmpty()) {
            message = Message.createErrorMessage("Invalid or missing datasetId: " + datasetId);
        } else {
            try {
                final long id = Long.parseLong(datasetId);

                // delete from datastore
                ofy().delete().type(Dataset.class).id(id).now();

                // invalidate memcache
                memcacheService.delete(DatasetServlet.getMemcacheKey(id));

                message = Message.createOkMessage("Dataset with id: " + id + " deleted");
            } catch (NumberFormatException nfe) {
                message = Message.createErrorMessage(nfe.getMessage());
            }
        }

        final String errorMessageAsJson = gson.toJson(message, Message.class);
        response.getWriter().println(errorMessageAsJson);
    }
}
