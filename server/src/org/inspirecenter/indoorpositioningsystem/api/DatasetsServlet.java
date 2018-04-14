package org.inspirecenter.indoorpositioningsystem.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;
import org.inspirecenter.indoorpositioningsystem.model.Datasets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class DatasetsServlet extends HttpServlet {

    static final String MEM_CACHE_DATASETS = "MEM_CACHE_DATASETS";

    private static final Gson gson = new Gson();
    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String magic = request.getParameter("magic");
        // todo check for magic

        String json = (String) memcacheService.get(MEM_CACHE_DATASETS);
        if(json == null) {
            final List<DatasetMetadata> datasetMetadataList = ofy().load().type(DatasetMetadata.class).list();
            final Datasets datasets = new Datasets(datasetMetadataList);
            json = gson.toJson(datasets, Datasets.class);
            memcacheService.put(MEM_CACHE_DATASETS, json);
        }
        response.getWriter().println(json);
    }
}