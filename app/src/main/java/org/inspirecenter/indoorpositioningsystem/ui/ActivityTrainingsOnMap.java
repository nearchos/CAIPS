package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

public class ActivityTrainingsOnMap extends AppCompatActivity {

    private Spinner floorSpinner;
    private TrainingView trainingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_on_map);

        floorSpinner = (Spinner) findViewById(R.id.activity_training_on_map_floor_spinner);
        trainingView = (TrainingView) findViewById(R.id.activity_training_on_map_training_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Training [] trainings = (Training []) intent.getSerializableExtra(ActivityTraining.PAYLOAD_TRAININGS_KEY);
        final String locationUuid = trainings[0].getLocationUuid();

        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);

        final Location location = DatabaseHelper.getLocation(databaseOpenHelper.getReadableDatabase(), locationUuid);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setTitle(getString(R.string.Training_map) + " - " + location.getName());

        final Floor [] floors = DatabaseHelper.getFloors(databaseOpenHelper.getReadableDatabase(), locationUuid);

        if(floors.length == 0) {
            Toast.makeText(this, R.string.No_floors_in_database, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            floorSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, floors));
            final Floor selectedFloor = floors[floorSpinner.getSelectedItemPosition()];
            trainingView.init(location, selectedFloor);
            floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final Floor selectedFloor = floors[position];
                    trainingView.init(location, selectedFloor, trainings);
                }

                @Override public void onNothingSelected(AdapterView<?> parent) { /* empty */ }
            });
        }
    }


}
