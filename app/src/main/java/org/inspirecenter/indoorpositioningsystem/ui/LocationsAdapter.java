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
import org.inspirecenter.indoorpositioningsystem.data.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Nearchos Paspallis
 * Created on 24/12/13.
 */
public class LocationsAdapter extends ArrayAdapter<Location>
{
    public static final String TAG = "org.codecyprus.android_client.ui.LocationsAdapter";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
    static
    {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final LayoutInflater layoutInflater;

    public LocationsAdapter(final Context context, final Location [] locations)
    {
        super(context, R.layout.list_item_location, locations);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_location, null);

        final Location location = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView locationName = (TextView) view.findViewById(R.id.list_item_location_name);
        final TextView locationCreatedBy = (TextView) view.findViewById(R.id.list_item_location_created_by);
        final TextView locationTimestamp = (TextView) view.findViewById(R.id.list_item_location_timestamp);

        // Bind the data efficiently with the holder.
        locationName.setText(location.getName());
        locationCreatedBy.setText(location.getCreatedBy());
        locationTimestamp.setText(SIMPLE_DATE_FORMAT.format(new Date(location.getTimestamp())));

        return view;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}