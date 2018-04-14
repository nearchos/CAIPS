package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class Datasets implements Serializable {

    private final List<DatasetMetadata> datasetsMetadata = new Vector<>();

    public Datasets(List<DatasetMetadata> datasetsMetadata) {
        this.datasetsMetadata.addAll(datasetsMetadata);
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

    public boolean isEmpty() {
        return datasetsMetadata.isEmpty();
    }
}