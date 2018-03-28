package org.inspirecenter.indoorpositioningsystem.model;

import java.util.List;
import java.util.Vector;

public class Datasets {

    private final List<DatasetMetadata> datasetsMetadata = new Vector<>();

    public Datasets(List<Dataset> datasets) {
        for(final Dataset dataset : datasets) {
            this.datasetsMetadata.add(new DatasetMetadata(dataset));
        }
    }

    public int getNumberOfDatasets() {
        return datasetsMetadata.size();
    }

    public DatasetMetadata getDatasetMetadata(final int index) {
        return datasetsMetadata.get(index);
    }

    public List<DatasetMetadata> getDatasetsMetadata() {
        return datasetsMetadata;
    }
}