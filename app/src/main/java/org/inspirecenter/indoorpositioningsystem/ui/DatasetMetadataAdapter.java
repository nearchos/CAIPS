package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Nearchos
 * Created: 10-Apr-18
 */
public class DatasetMetadataAdapter extends ArrayAdapter<DatasetMetadata> {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
    static
    {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final LayoutInflater layoutInflater;

    DatasetMetadataAdapter(@NonNull Context context, @NonNull List<DatasetMetadata> objects) {
        super(context, R.layout.list_item_dataset_metadata, objects);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_dataset_metadata, null);

        final DatasetMetadata datasetMetadata = getItem(position);
        assert datasetMetadata != null;

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView datasetMetadataDescription = view.findViewById(R.id.list_item_dataset_metadata_description);
        final TextView datasetMetadataCreatedBy = view.findViewById(R.id.list_item_dataset_metadata_created_by);
        final TextView datasetMetadataTimestamp = view.findViewById(R.id.list_item_dataset_metadata_timestamp);

        Log.d("caips", "view: " + view);
        Log.d("caips", "datasetMetadata: " + datasetMetadata);
        // Bind the data efficiently with the holder.
        datasetMetadataDescription.setText(datasetMetadata.getDescription());
        datasetMetadataCreatedBy.setText(datasetMetadata.getCreatedBy());
        datasetMetadataTimestamp.setText(SIMPLE_DATE_FORMAT.format(new Date(datasetMetadata.getTimestamp())));

        return view;
    }
}