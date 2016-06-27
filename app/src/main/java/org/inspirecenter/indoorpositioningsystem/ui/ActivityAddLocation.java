package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ActivityAddLocation extends AppCompatActivity {

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

        final String uuid = UUID.randomUUID().toString();
        final String createdBy = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        final long timestamp = System.currentTimeMillis();

        uuidEditText.setText(uuid);
        createdByEditText.setText(createdBy);
        timestampEditText.setText(SIMPLE_DATE_FORMAT.format(new Date(timestamp)));
        nameEditText.selectAll();
        nameEditText.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("name", nameEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final String name = savedInstanceState.getString("name");
        nameEditText.setText(name);
    }

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
        final String createdBy = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        final Location location = new Location(UUID.randomUUID().toString(), nameEditText.getText().toString(), createdBy, System.currentTimeMillis());
        DatabaseHelper.add(databaseOpenHelper.getWritableDatabase(), location);
        Snackbar.make(root, "Location added", Snackbar.LENGTH_SHORT).show();
        finish();
    }
}