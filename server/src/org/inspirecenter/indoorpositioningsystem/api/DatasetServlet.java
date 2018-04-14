package org.inspirecenter.indoorpositioningsystem.api;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.Message;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DatasetServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String magic = request.getParameter("magic");
        // todo check for magic

        final String datasetIdS = request.getParameter("datasetId");
        try {
            final long datasetId = Long.parseLong(datasetIdS);

            final Key<DatasetMetadata> key = Key.create(DatasetMetadata.class, datasetId);
            final DatasetMetadata datasetMetadata = ofy().load().key(key).now();

            if(datasetMetadata == null) {
                final Message message = Message.createErrorMessage("Could not find DatasetEntity with id: " + datasetId);
                response.getWriter().println(gson.toJson(message));
            } else {
                final List<MeasurementEntry> measurementEntries = ofy().load().type(MeasurementEntry.class).filter("datasetId", datasetId).list();
                final Dataset dataset = new Dataset(datasetMetadata, measurementEntries);
                final String json = gson.toJson(dataset, Dataset.class);
                response.getWriter().println(json);
            }
        } catch (NumberFormatException nfe) {
            final Message message = Message.createErrorMessage("Could not parse datasetId parameter (invalid long format): " + datasetIdS);
            response.getWriter().println(gson.toJson(message));
        }
    }
}