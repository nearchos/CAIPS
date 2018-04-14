package org.inspirecenter.indoorpositioningsystem.api;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.Message;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class DeleteDatasetServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

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
                Iterable<Key<MeasurementEntry>> allKeys = ofy().load().type(MeasurementEntry.class).filter("datasetId=", datasetId).keys();
                ofy().delete().key((Key<?>) allKeys);
                ofy().delete().type(Dataset.class).id(id).now();

                message = Message.createOkMessage("Dataset with id: " + id + " deleted");
            } catch (NumberFormatException nfe) {
                message = Message.createErrorMessage(nfe.getMessage());
            }
        }

        final String messageAsJson = gson.toJson(message, Message.class);
        response.getWriter().println(messageAsJson);
    }
}
