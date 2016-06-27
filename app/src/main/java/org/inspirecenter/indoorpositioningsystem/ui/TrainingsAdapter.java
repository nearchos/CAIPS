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
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Training;

/**
 * @author Nearchos Paspallis
 * Created on 24/12/13.
 */
public class TrainingsAdapter extends ArrayAdapter<Training> {

    public static final String TAG = "ips";

    private final LayoutInflater layoutInflater;

    public TrainingsAdapter(final Context context, final Training [] trainings)
    {
        super(context, R.layout.list_item_training, trainings);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_training, null);

        final Training training = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView timestamp = (TextView) view.findViewById(R.id.list_item_training_timestamp);
        final TextView coordinates = (TextView) view.findViewById(R.id.list_item_training_coordinates);

        // Bind the data efficiently with the holder.
        timestamp.setText(training.getTimestampAsString());
        coordinates.setText(training.getCoordinatesAsString());

        return view;
    }
}