package org.inspirecenter.indoorpositioningsystem.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DatasetServlet extends HttpServlet {

    private static final String MEM_CACHE_DATASET = "MEM_CACHE_DATASET-%id";

    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private static final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // todo check for magic
        final long datasetId = 0L; // todo get from parameters

        String json = (String) memcacheService.get(getMemcacheKey(datasetId));
        if(json == null) {
            final Dataset dataset = ofy().load().type(Dataset.class).id(datasetId).now();
            json = gson.toJson(dataset, Dataset.class);
            memcacheService.put(MEM_CACHE_DATASET.replace("%id", Long.toString(datasetId)), gson);
        }
        response.getWriter().println(json);
    }

    static String getMemcacheKey(final long id) {
        return MEM_CACHE_DATASET.replace("%id", Long.toString(id));
    }
}