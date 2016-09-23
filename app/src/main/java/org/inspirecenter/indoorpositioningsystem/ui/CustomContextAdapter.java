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
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.CustomContextElement;
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
class CustomContextAdapter extends ArrayAdapter<CustomContextElement>
{
    public static final String TAG = "org.codecyprus.android_client.ui.CustomContextAdapter";

    private final LayoutInflater layoutInflater;

    CustomContextAdapter(final Context context, final CustomContextElement[] customContextElements) {
        super(context, R.layout.list_item_custom_context, customContextElements);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
    {
        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_custom_context, null);

        final CustomContextElement customContextElement = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final TextView customContextName = (TextView) view.findViewById(R.id.list_item_custom_context_name);
        final TextView customContextValue = (TextView) view.findViewById(R.id.list_item_custom_context_value);
        final CheckBox customContextChecked = (CheckBox) view.findViewById(R.id.list_item_custom_context_checked);

        // Bind the data efficiently with the holder.
        assert customContextElement != null;
        customContextName.setText(customContextElement.getName());
        customContextValue.setText(customContextElement.getValue());
        customContextChecked.setChecked(customContextElement.isChecked());

        customContextChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(getContext());
                final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
                DatabaseHelper.editCustomContext(sqLiteDatabase, customContextElement.getUuid(), b);
                sqLiteDatabase.close();
            }
        });

        return view;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}