package org.inspirecenter.indoorpositioningsystem.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.googlecode.objectify.Key;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.Message;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class CreateDatasetServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

//        final String magic = request.getParameter("magic");
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

            if(dataset == null) {
                message = Message.createErrorMessage("Null dataset");
            } else {
                final Key<DatasetMetadata> key = ofy().save().entity(new DatasetMetadata(dataset)).now();
                final DatasetMetadata datasetMetadata = ofy().load().key(key).now();

                final List<MeasurementEntry> adjustedMeasurementEntries = getAdjustedMeasurementEntries(dataset.getMeasurementEntries(), datasetMetadata.getId());
                // write in batches (cannot write more than 500 in each batch)
                final int batchSize = 250;
                while(!adjustedMeasurementEntries.isEmpty()) {
                    final Vector<MeasurementEntry> batchOfMeasurementEntries = new Vector<>();
                    final int currentSize = adjustedMeasurementEntries.size();
                    for(int i = 0; i < Math.min(batchSize, currentSize); i++) {
                        batchOfMeasurementEntries.add(adjustedMeasurementEntries.remove(0));
                    }
                    ofy().save().entities(batchOfMeasurementEntries).now();
                    // wait 1 second between each batch
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { log("interrupted: " + ie.getMessage()); }
                }

                // invalidate memcache
                memcacheService.delete(DatasetsServlet.MEM_CACHE_DATASETS);

                message = Message.createOkMessage("Dataset created with id: " + dataset.getId());
            }
        } catch (JsonSyntaxException jse) {
            message = Message.createErrorMessage(jse.getMessage());
        }

        final String errorMessageAsJson = gson.toJson(message, Message.class);
        response.getWriter().println(errorMessageAsJson);
    }

    private List<MeasurementEntry> getAdjustedMeasurementEntries(final List<MeasurementEntry> measurementEntries, final Long datasetId) {
        final Vector<MeasurementEntry> adjustedMeasurementEntries = new Vector<>();
        for(final MeasurementEntry measurementEntry : measurementEntries) {
            adjustedMeasurementEntries.add(new MeasurementEntry(
                    measurementEntry.getUuid(),
                    datasetId,
                    measurementEntry.getFloorUUID(),
                    measurementEntry.getCreatedBy(),
                    measurementEntry.getTimestamp(),
                    measurementEntry.getCoordinates(),
                    measurementEntry.getContextEntries(),
                    measurementEntry.getRadioDataEntries()));
        }
        return adjustedMeasurementEntries;
    }
}