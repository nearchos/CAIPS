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
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Nearchos Paspallis
 * Created on 24/12/13.
 */
public class FloorsAdapter extends ArrayAdapter<Floor>
{
    public static final String TAG = "org.codecyprus.android_client.ui.FloorsAdapter";

    private final LayoutInflater layoutInflater;
    private final Map<String,Integer> floorUuidToTrainingsMap = new HashMap<>();

    public FloorsAdapter(final Context context, final Floor[] floors) {
        super(context, R.layout.list_item_floor, floors);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(floors.length > 0) {
            final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(context);
            final Training [] trainings = DatabaseHelper.getTrainings(databaseOpenHelper.getReadableDatabase(), floors[0].getLocationUUID());
            for(final Floor floor : floors) {
                floorUuidToTrainingsMap.put(floor.getUuid(), 0);
            }
            for(final Training training : trainings) {
                int num = floorUuidToTrainingsMap.get(training.getFloorUuid()) + 1;
                floorUuidToTrainingsMap.put(training.getFloorUuid(), num);
            }
        }
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_floor, null);

        final Floor floor = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView floorName = (TextView) view.findViewById(R.id.list_item_floor_name);
        final TextView floorImageURL = (TextView) view.findViewById(R.id.list_item_floor_url);

        // Bind the data efficiently with the holder.
        floorName.setText(String.format(Locale.US, "[%2d] %s (%d trainings)", floor.getSeq(), floor.getName(), floorUuidToTrainingsMap.get(floor.getUuid())));
        floorImageURL.setText(floor.getImageUrl());

        return view;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}