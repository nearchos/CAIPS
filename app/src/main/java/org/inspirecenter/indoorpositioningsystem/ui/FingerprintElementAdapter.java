package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.FingerprintElement;

import java.util.List;

/**
 * @author Nearchos
 *         Created: 25-Apr-16
 */
public class FingerprintElementAdapter extends ArrayAdapter<FingerprintElement> {

    private final LayoutInflater layoutInflater;

    public FingerprintElementAdapter(final Context context, final List<FingerprintElement> fingerprintElements) {
        super(context, R.layout.fingerprint_element_item, fingerprintElements);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View fingerprintElementView;

        if (convertView == null) {
            fingerprintElementView = layoutInflater.inflate(R.layout.fingerprint_element_item, null);
        } else {
            fingerprintElementView = convertView;
        }

        final FingerprintElement fingerprintElement = getItem(position);
        final TextView bssidTextView = (TextView) fingerprintElementView.findViewById(R.id.fingerprint_bssid);
        bssidTextView.setText(fingerprintElement.getSsid());
        final TextView detailsTextView = (TextView) fingerprintElementView.findViewById(R.id.fingerprint_details);
        detailsTextView.setText(fingerprintElement.getDetails());

        return fingerprintElementView;
    }
}
