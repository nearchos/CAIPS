package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

public class ActivityLocations extends AppCompatActivity {

    private ListView locationsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        locationsListView = (ListView) findViewById(R.id.activity_locations_list_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final String username = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        final String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_AUTH_TOKEN, null);

        if(username == null || username.isEmpty()) {
            Toast.makeText(this, "Must select an account first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ActivityAuthenticate.class));
        } else if(authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Expired session - Please select an account first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ActivityAuthenticate.class));
        } else {
            final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
            final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM menu_locations WHERE createdBy=?", new String [] { username });
            int numOfRows = cursor.getCount();
            cursor.moveToFirst();
            int columnUUIDIndex = cursor.getColumnIndex("uuid");
            int columnNameIndex = cursor.getColumnIndex("name");
            int columnCreatedByIndex = cursor.getColumnIndex("createdBy");
            int columnTimestampIndex = cursor.getColumnIndex("timestamp");
            final Location [] locations = new Location[numOfRows];
            for(int i = 0; i < numOfRows; i++) {
                final Location location = new Location(
                        cursor.getString(columnUUIDIndex),
                        cursor.getString(columnNameIndex),
                        cursor.getString(columnCreatedByIndex),
                        cursor.getLong(columnTimestampIndex)
                );
                locations[i] = location;
                cursor.moveToNext();
            }
            cursor.close();

            locationsListView.setAdapter(new LocationsAdapter(this, locations));
            locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Intent intent = new Intent(ActivityLocations.this, ActivityLocation.class);
                    intent.putExtra(ActivityLocation.INTENT_EXTRA_LOCATION_UUID_KEY, locations[position].getUuid());
                    startActivity(intent);
                }
            });
        }
    }

    public void add(View view) {
        startActivity(new Intent(ActivityLocations.this, ActivityAddLocation.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, ActivityContextSettings.class));
                return true;
            case R.id.menu_about:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}