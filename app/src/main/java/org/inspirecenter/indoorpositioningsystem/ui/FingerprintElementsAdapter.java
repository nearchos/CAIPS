/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.FingerprintElement;
import org.inspirecenter.indoorpositioningsystem.data.Training;

/**
 * @author Nearchos Paspallis
 * Created on 24/12/13.
 */
public class FingerprintElementsAdapter extends ArrayAdapter<FingerprintElement> {

    public static final String TAG = "ips";

    private final LayoutInflater layoutInflater;

    public FingerprintElementsAdapter(final Context context, final FingerprintElement [] fingerprintElements)
    {
        super(context, R.layout.list_item_fingerprint_element, fingerprintElements);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_fingerprint_element, null);

        final FingerprintElement fingerprintElement = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView bssidTextView = (TextView) view.findViewById(R.id.list_item_fingerprint_element_bssid);
        final TextView signalStrengthTextView = (TextView) view.findViewById(R.id.list_item_fingerprint_element_signal_strength);
        final TextView frequencyTextView = (TextView) view.findViewById(R.id.list_item_fingerprint_element_frequency);

        assert fingerprintElement != null;

        // Bind the data efficiently with the holder.
        bssidTextView.setText(fingerprintElement.getSsid());
        signalStrengthTextView.setText(fingerprintElement.getLevelAsDecibel() + "dB (" + fingerprintElement.getLevelAsRatio() + ")");
        frequencyTextView.setText(fingerprintElement.getFrequency() + "MHz");

        return view;
    }
}