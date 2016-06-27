package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ActivityEditLocation extends AppCompatActivity {

    public static final String INTENT_EXTRA_LOCATION_UUID_KEY = "edited_location_uuid";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private View root;

    private EditText uuidEditText;
    private EditText createdByEditText;
    private EditText nameEditText;
    private EditText timestampEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_location);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        root = findViewById(R.id.activity_add_or_edit_location_root);

        uuidEditText = (EditText) findViewById(R.id.location_uuid);
        createdByEditText = (EditText) findViewById(R.id.location_created_by);
        nameEditText = (EditText) findViewById(R.id.location_name);
        if(nameEditText != null) nameEditText.requestFocus();
        timestampEditText = (EditText) findViewById(R.id.location_timestamp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String locationUuid = intent.getStringExtra(INTENT_EXTRA_LOCATION_UUID_KEY);

        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final Location location = DatabaseHelper.getLocation(databaseOpenHelper.getReadableDatabase(), locationUuid);

        uuidEditText.setText(location.getUuid());
        createdByEditText.setText(location.getCreatedBy());
        timestampEditText.setText(SIMPLE_DATE_FORMAT.format(new Date(location.getTimestamp())));
        nameEditText.setText(location.getName());
        nameEditText.selectAll();
        nameEditText.requestFocus();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString("uuid", uuidEditText.getText().toString());
//        outState.putString("createdBy", createdByEditText.getText().toString());
//        outState.putString("name", nameEditText.getText().toString());
//        outState.putString("timestamp", timestampEditText.getText().toString());
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        final String uuid = savedInstanceState.getString("uuid");
//        nameEditText.setText(uuid);
//        final String createdBy = savedInstanceState.getString("createdBy");
//        nameEditText.setText(createdBy);
//        final String name = savedInstanceState.getString("name");
//        nameEditText.setText(name);
//        final String timestamp = savedInstanceState.getString("timestamp");
//        nameEditText.setText(timestamp);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void save(View view) {
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        DatabaseHelper.edit(databaseOpenHelper.getWritableDatabase(), uuidEditText.getText().toString(), nameEditText.getText().toString());
        Snackbar.make(root, "Location edited", Snackbar.LENGTH_SHORT).show();
        finish();
    }
}