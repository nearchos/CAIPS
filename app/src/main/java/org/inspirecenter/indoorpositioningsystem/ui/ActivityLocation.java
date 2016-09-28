package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ActivityLocation extends AppCompatActivity {

    public static final String TAG = "ips";

    public static final String INTENT_EXTRA_LOCATION_UUID_KEY = "location_uuid";

    private EditText uuidEditText;
    private EditText createdByEditText;
    private EditText nameEditText;
    private EditText timestampEditText;

    private ListView floorsListView;

    private TextView numOfTrainingsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        uuidEditText = (EditText) findViewById(R.id.location_uuid);
        createdByEditText = (EditText) findViewById(R.id.location_created_by);
        nameEditText = (EditText) findViewById(R.id.location_name);
        timestampEditText = (EditText) findViewById(R.id.location_timestamp);

        floorsListView = (ListView) findViewById(R.id.activity_location_floors);

        numOfTrainingsTextView = (TextView) findViewById(R.id.activity_location_num_of_trainings_text_view);

        // make sure soft keyboard is not shown until needed
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private Location location;

    @Override
    protected void onResume() {
        super.onResume();

        final String locationUUID = getIntent().getStringExtra(INTENT_EXTRA_LOCATION_UUID_KEY);
        location = getLocation(locationUUID);

        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final int numOfTrainings = DatabaseHelper.getNumOfTrainings(databaseOpenHelper.getReadableDatabase(), locationUUID);
        numOfTrainingsTextView.setText(getString(R.string.Num_of_trainings, numOfTrainings));

        uuidEditText.setText(location.getUuid());
        createdByEditText.setText(location.getCreatedBy());
        timestampEditText.setText(ActivityAddLocation.SIMPLE_DATE_FORMAT.format(new Date(location.getTimestamp())));
        nameEditText.setText(location.getName());

        final String username = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        final String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_AUTH_TOKEN, null);

        if(username == null || username.isEmpty()) {
            Toast.makeText(this, "Must select an account first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ActivityAuthenticate.class));
        } else if(authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Expired session - Please select an account first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ActivityAuthenticate.class));
        } else {
            final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
            final Floor [] floors = DatabaseHelper.getFloors(sqLiteDatabase, locationUUID);
            Arrays.sort(floors, new Comparator<Floor>() {
                @Override
                public int compare(Floor lhs, Floor rhs) {
                    return (int) (rhs.getSeq() - lhs.getSeq());
                }
            });

            Log.d(TAG, "Fetched floors: " + Arrays.toString(floors));
            floorsListView.setAdapter(new FloorsAdapter(this, floors));
            floorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Intent intent = new Intent(ActivityLocation.this, ActivityEditFloor.class);
                    intent.putExtra(ActivityEditFloor.INTENT_EXTRA_FLOOR_UUID_KEY, floors[position].getUuid());
                    startActivity(intent);
                }
            });
        }

        floorsListView.requestFocus();
    }

    /**
     * Returns the menu_location that corresponds to the given uuid.
     *
     * @param uuid
     * @return the menu_location that corresponds to the given uuid or null if not found
     */
    private Location getLocation(final String uuid) {
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM menu_locations WHERE uuid=?", new String [] {uuid});
        cursor.moveToFirst();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnCreatedByIndex = cursor.getColumnIndex("createdBy");
        int columnTimestampIndex = cursor.getColumnIndex("timestamp");
        int numOfRows = cursor.getCount();
        final Location location;
        if(numOfRows < 1) {
            location = null;
        } else {
            location = new Location(
                    cursor.getString(columnUUIDIndex),
                    cursor.getString(columnNameIndex),
                    cursor.getString(columnCreatedByIndex),
                    cursor.getLong(columnTimestampIndex));
        }
        cursor.close();

        return location;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("location", location);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        location = (Location) savedInstanceState.getSerializable("location");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_location_edit:
                final Intent intent = new Intent(this, ActivityEditLocation.class);
                intent.putExtra(ActivityEditLocation.INTENT_EXTRA_LOCATION_UUID_KEY, location.getUuid());
                startActivity(intent);
                return true;
            case R.id.menu_location_trainings:
                viewTrainings(null);
                return true;
            case R.id.menu_location_indoors_positioning:
                final Intent startActivityAuthenticateIntentForLocation = new Intent(this, ActivityAuthenticate.class);
                startActivityAuthenticateIntentForLocation.putExtra("nextActivity", ActivityIndoorsLocation.class);
                startActivity(startActivityAuthenticateIntentForLocation);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFloor(View view) {
        final Intent intent = new Intent(this, ActivityAddFloor.class);
        intent.putExtra(ActivityAddFloor.INTENT_EXTRA_LOCATION_UUID_KEY, location.getUuid());
        startActivity(intent);
    }

    public void viewTrainings(View view) {
        final Intent startTrainingIntent = new Intent(this, ActivityTrainings.class);
        startTrainingIntent.putExtra(ActivityTrainings.INTENT_EXTRA_LOCATION_UUID_KEY, location.getUuid());
        startActivity(startTrainingIntent);
    }
}