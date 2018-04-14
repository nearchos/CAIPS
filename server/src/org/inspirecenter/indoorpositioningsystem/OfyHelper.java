package org.inspirecenter.indoorpositioningsystem;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.Parameter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OfyHelper implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ObjectifyService.init();

        // This will be invoked as part of a warm-up request, or the first user request if no warm-up request.
        ObjectifyService.register(DatasetMetadata.class);
        ObjectifyService.register(MeasurementEntry.class);
        ObjectifyService.register(Parameter.class);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}