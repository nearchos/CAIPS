package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.FingerprintElement;
import org.inspirecenter.indoorpositioningsystem.data.Training;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ActivityTraining extends AppCompatActivity {

    public static final String TAG = "ips";

    public static final String PAYLOAD_LOCATION_UUID_KEY = "payload-location-uuid-key";
    public static final String PAYLOAD_TRAINING_INDEX_KEY = "payload-training-index-key";
    public static final String PAYLOAD_TRAINING_UUID_KEY = "payload-trainings-key";

    private TextView uuidTextView;
    private TextView coordinatesTextView;
    private TextView timestampTextView;
    private ListView scanResultsListView;
    private ListView contextListView;

    private int selectedTrainingIndex = 0;
    private Training [] trainings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        uuidTextView = (TextView) findViewById(R.id.activity_training_uuid);
        coordinatesTextView = (TextView) findViewById(R.id.activity_training_coordinates);
        timestampTextView = (TextView) findViewById(R.id.activity_training_timestamp);
        scanResultsListView = (ListView) findViewById(R.id.activity_training_list_view_scan_results);
        contextListView = (ListView) findViewById(R.id.activity_training_list_view_context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_training_view_map:
                final Training selectedTraining = trainings[selectedTrainingIndex];
                final Intent intent = new Intent(this, ActivityTrainingsOnMap.class);
                intent.putExtra(PAYLOAD_TRAINING_UUID_KEY, selectedTraining.getUUID());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(final Intent intent) {
        assert intent != null;

        selectedTrainingIndex = intent.getIntExtra(PAYLOAD_TRAINING_INDEX_KEY, 0);
        final String locationUuid = intent.getStringExtra(PAYLOAD_LOCATION_UUID_KEY);
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        trainings = DatabaseHelper.getTrainingsByLocationUuid(databaseOpenHelper.getReadableDatabase(), locationUuid);
        databaseOpenHelper.close();
        showSelectedTraining();
    }

    private void showSelectedTraining() {

        final Training training = trainings[selectedTrainingIndex];

        uuidTextView.setText(training.getUUID());
        coordinatesTextView.setText("(" + training.getLat() + ", " + training.getLng() + ")");
        timestampTextView.setText(new Date(training.getTimestamp()).toString());

        try {
            final String radiomapAsJSON = training.getRadiomapAsJSON();
            final JSONArray radiomapJsonArray = new JSONArray(radiomapAsJSON);
            final FingerprintElement [] fingerprintElements = new FingerprintElement[radiomapJsonArray.length()];
            for(int i = 0; i < fingerprintElements.length; i++) {
                final JSONObject fingerprintElementObject = radiomapJsonArray.getJSONObject(i);
                fingerprintElements[i] = new FingerprintElement(fingerprintElementObject.getString("bssid"), fingerprintElementObject.getInt("level"), fingerprintElementObject.getInt("frequency"));
            }
            scanResultsListView.setAdapter(new FingerprintElementsAdapter(this, fingerprintElements));
        } catch (JSONException jsone) {
            Log.e(TAG, jsone.getMessage());
        }

        try {
            final String contextAsJSON = training.getContextAsJSON();
            final JSONArray contextJsonArray = new JSONArray(contextAsJSON);
            final String [] contextElements = new String[contextJsonArray.length()];
            for(int i = 0; i < contextElements.length; i++) {
                final JSONObject contextObject = contextJsonArray.getJSONObject(i);
                contextElements[i] = contextObject.toString();
            }
            contextListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contextElements));
        } catch (JSONException jsone) {
            Log.e(TAG, jsone.getMessage());
        }
    }

    public void next(final View view) {
        if(selectedTrainingIndex == trainings.length - 1) {
            Toast.makeText(this, "Already showing the last element", Toast.LENGTH_SHORT).show();
        } else {
            selectedTrainingIndex++;
            showSelectedTraining();
        }
    }

    public void previous(final View view) {
        if(selectedTrainingIndex == 0) {
            Toast.makeText(this, "Already showing the first element", Toast.LENGTH_SHORT).show();
        } else {
            selectedTrainingIndex--;
            showSelectedTraining();
        }
    }
}