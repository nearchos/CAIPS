package org.inspirecenter.indoorpositioningsystem.ui;

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityTrainings extends AppCompatActivity {

    public static final String TAG = "ips";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US);

    public static final String INTENT_EXTRA_LOCATION_UUID_KEY = "location_uuid";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        this.listView = findViewById(R.id.activity_trainings_list_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onNewIntent(getIntent());
    }

    private String locationUuid;

    private void updateListView() {
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final Training [] trainings = DatabaseHelper.getTrainingsByLocationUuid(databaseOpenHelper.getReadableDatabase(), locationUuid);
        databaseOpenHelper.close();
        Log.d(TAG, "trainings: " + trainings.length);
        listView.setAdapter(new TrainingsAdapter(this, trainings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // using custom training viewer
                final Intent intent = new Intent(ActivityTrainings.this, ActivityTraining.class);
                intent.putExtra(ActivityTraining.PAYLOAD_TRAINING_INDEX_KEY, position);
                intent.putExtra(ActivityTraining.PAYLOAD_LOCATION_UUID_KEY, locationUuid);
                startActivity(intent);
//                Toast.makeText(ActivityTrainings.this, "Radiomap: " + training.getRadiomapAsJSON(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(ActivityTrainings.this, "Context: " + training.getContextAsJSON(), Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Training training = trainings[position];
                deleteTraining(training);
                return true;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        locationUuid = intent.getStringExtra(INTENT_EXTRA_LOCATION_UUID_KEY);
        if(locationUuid == null) finish();

        updateListView();
    }

    private void deleteTraining(final Training training) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(ActivityTrainings.this);
                        DatabaseHelper.deleteTraining(databaseOpenHelper.getWritableDatabase(), training.getUUID());
                        updateListView();
                        Toast.makeText(ActivityTrainings.this, R.string.Deleted, Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(ActivityTrainings.this, R.string.Cancelled, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.Delete_training)
                .setPositiveButton(R.string.Yes, dialogClickListener)
                .setNegativeButton(R.string.No, dialogClickListener)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trainings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.menu_trainings_on_map: {
                final Intent intent = new Intent(this, ActivityTrainingsOnMap.class);
                intent.putExtra(ActivityTraining.PAYLOAD_LOCATION_UUID_KEY, locationUuid);
                startActivity(intent);
                return true;
            }
            case R.id.menu_trainings_context_settings: {
                startActivity(new Intent(this, ActivityContextSettings.class));
                return true;
            }
            case R.id.menu_trainings_custom_context: {
                final Intent intent = new Intent(this, ActivityCustomContext.class);
                final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
                final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
                final Location location = DatabaseHelper.getLocation(sqLiteDatabase, locationUuid);
                intent.putExtra("location", location);
                startActivity(intent);
                return true;
            }
            case R.id.menu_trainings_export: {
                exportTrainings();
                return true;
            }
            case R.id.menu_trainings_delete_all: {
                deleteAllTrainings();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 42;

    private void exportTrainings() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String [] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            saveToFile();
        }
    }

    private void deleteAllTrainings() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(ActivityTrainings.this);
                        final Location location = DatabaseHelper.getLocation(databaseOpenHelper.getReadableDatabase(), locationUuid);
                        final int numOfDeletedTrainings = DatabaseHelper.deleteAllTrainings(databaseOpenHelper.getWritableDatabase(), locationUuid);
                        updateListView();
                        Toast.makeText(ActivityTrainings.this, getString(R.string.Deleted_all, numOfDeletedTrainings, location.getName()), Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(ActivityTrainings.this, R.string.Cancelled, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.Delete_all_trainings)
                .setPositiveButton(R.string.Yes, dialogClickListener)
                .setNegativeButton(R.string.No, dialogClickListener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveToFile();
        }
    }

    private void saveToFile() {
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final Training[] trainings = DatabaseHelper.getTrainingsByLocationUuid(databaseOpenHelper.getReadableDatabase(), locationUuid);

        final StringBuilder json = new StringBuilder("[\n");
        int num = 0;
        for (final Training training : trainings) {
            json.append("{ \"uuid\": \"").append(training.getUUID())
                    .append("\", \"locationUuid\": \"").append(training.getLocationUuid())
                    .append("\", \"floorUuid\": \"").append(training.getFloorUuid())
                    .append("\", \"createdBy\": \"").append(training.getCreatedBy())
                    .append("\", \"timestamp\": ").append(training.getTimestamp())
                    .append(", \"lat\": ").append(training.getLat())
                    .append(", \"lng\": ").append(training.getLng())
                    .append(", \"radiomap\": ").append(training.getRadiomapAsJSON())
                    .append(", \"context\": ").append(training.getContextAsJSON());
            json.append(++num < trainings.length ? " },\n" : " }\n");
        }
        json.append("]\n");

        final File sdCard = Environment.getExternalStorageDirectory();
        Log.d(TAG, "sdCard: " + sdCard);
        Log.d(TAG, "sdCard.exists(): " + sdCard.exists());
        final File dir = new File(sdCard.getAbsolutePath() + "/ips");
        if (dir.exists() || dir.mkdirs()) {
            File file = new File(dir, "fingerprint-" + SIMPLE_DATE_FORMAT.format(new Date()) + ".json");
            try {
                final PrintWriter printWriter = new PrintWriter(file);
                printWriter.println(json.toString());
                printWriter.close();
                Toast.makeText(this, "File containing " + trainings.length + " trainings created in: " + dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException fnfe) {
                Toast.makeText(this, "Failed to create file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create dir: " + dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }

    public void addTraining(View view) {
        final Intent intent = new Intent(ActivityTrainings.this, ActivitySubmitTraining.class);
        intent.putExtra(ActivitySubmitTraining.INTENT_EXTRA_LOCATION_UUID_KEY, locationUuid);
        startActivity(intent);
    }
}
