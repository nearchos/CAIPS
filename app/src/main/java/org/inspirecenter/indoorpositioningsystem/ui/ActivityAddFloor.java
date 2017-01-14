package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Image;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.util.Locale;
import java.util.UUID;

public class ActivityAddFloor extends AppCompatActivity {

    public static final String TAG = "ips";

    public static final String INTENT_EXTRA_LOCATION_UUID_KEY = "location_uuid";

    private EditText uuidEditText;
    private EditText locationEditText;
    private EditText nameEditText;
    private EditText seqEditText;
    private EditText imageUuidEditText;
    private ImageView imageView;
    private EditText topLeftEditText;
    private EditText bottomRightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_floor);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        uuidEditText        = (EditText) findViewById(R.id.floor_uuid);
        locationEditText    = (EditText) findViewById(R.id.floor_location_uuid);
        nameEditText        = (EditText) findViewById(R.id.floor_name);
        if(nameEditText != null) nameEditText.requestFocus();
        seqEditText         = (EditText) findViewById(R.id.floor_seq);
        topLeftEditText     = (EditText) findViewById(R.id.floor_top_left);
        bottomRightEditText = (EditText) findViewById(R.id.floor_bottom_right);
        imageUuidEditText = (EditText) findViewById(R.id.floor_image);
        imageView           = (ImageView) findViewById(R.id.activity_add_or_edit_floor_image_view);

        final String uuid = UUID.randomUUID().toString();

        uuidEditText.setText(uuid);
        seqEditText.setText(String.format(Locale.US, "%d", 1));
        topLeftEditText.setText(String.format(Locale.US, "%.6f, %.6f", 0d, 0d));
        bottomRightEditText.setText(String.format(Locale.US, "%.6f, %.6f", 0d, 0d));

        final String imageUuid = imageUuidEditText.getText().toString();
        if(!imageUuid.isEmpty()) {
            final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            final Image image = DatabaseHelper.getImage(databaseOpenHelper.getReadableDatabase(), imageUuid);
            imageView.setImageBitmap(image.getImage());
        }
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

        locationEditText.setText(locationUuid);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("uuid", uuidEditText.getText().toString());
        outState.putString("locationUuid", locationEditText.getText().toString());
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("seq", seqEditText.getText().toString());
        outState.putString("topLeft", topLeftEditText.getText().toString());
        outState.putString("bottomRight", bottomRightEditText.getText().toString());
        outState.putString("imageUuid", imageUuidEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final String uuid = savedInstanceState.getString("uuid");
        uuidEditText.setText(uuid);
        final String locationUuid = savedInstanceState.getString("locationUuid");
        locationEditText.setText(locationUuid);
        final String name = savedInstanceState.getString("name");
        nameEditText.setText(name);
        final String seq = savedInstanceState.getString("seq");
        seqEditText.setText(seq);
        final String topLeft = savedInstanceState.getString("topLeft");
        topLeftEditText.setText(topLeft);
        final String bottomRight = savedInstanceState.getString("bottomRight");
        bottomRightEditText.setText(bottomRight);
        final String imageUuid = savedInstanceState.getString("imageUuid");
        imageUuidEditText.setText(imageUuid);
        final Image image = DatabaseHelper.getImage(new DatabaseOpenHelper(this).getReadableDatabase(), imageUuid);
        if(image != null) {
            imageView.setImageBitmap(image.getImage());
        }
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

    public static final int REQUEST_CODE_EDIT_TOP_LEFT = 10;
    public static final int REQUEST_CODE_EDIT_BOTTOM_RIGHT = 20;
    public static final int REQUEST_CODE_SELECT_IMAGE = 30;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_EDIT_TOP_LEFT) {
                double lat = data.getDoubleExtra(ActivitySelectCoordinates.SELECTED_LAT, 0d);
                double lng = data.getDoubleExtra(ActivitySelectCoordinates.SELECTED_LNG, 0d);
                topLeftEditText.setText(String.format(Locale.US, "%.6f, %.6f", lat, lng));
            } else if(requestCode == REQUEST_CODE_EDIT_BOTTOM_RIGHT) {
                double lat = data.getDoubleExtra(ActivitySelectCoordinates.SELECTED_LAT, 0d);
                double lng = data.getDoubleExtra(ActivitySelectCoordinates.SELECTED_LNG, 0d);
                bottomRightEditText.setText(String.format(Locale.US, "%.6f, %.6f", lat, lng));
            } else if(requestCode == REQUEST_CODE_SELECT_IMAGE) {
                final String imageUuid = data.getStringExtra(ActivitySelectImage.INTENT_EXTRA_SELECTED_IMAGE_UUID_KEY);
                imageUuidEditText.setText(imageUuid);
                final Image image = DatabaseHelper.getImage(new DatabaseOpenHelper(this).getReadableDatabase(), imageUuid);
                imageView.setImageBitmap(image.getImage());
            }
        } else if(resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.Cancelled, Toast.LENGTH_SHORT).show();
        }
    }

    public void editTopLeft(View view) {
        final Intent intent = new Intent(this, ActivitySelectCoordinates.class);
        final String topLeftS = topLeftEditText.getText().toString();
        double topLeftLat = Double.parseDouble(topLeftS.substring(0, topLeftS.indexOf(',')));
        double topLeftLng = Double.parseDouble(topLeftS.substring(topLeftS.indexOf(',') + 1));
        final String bottomRightS = bottomRightEditText.getText().toString();
        double bottomRightLat = Double.parseDouble(bottomRightS.substring(0, bottomRightS.indexOf(',')));
        double bottomRightLng = Double.parseDouble(bottomRightS.substring(bottomRightS.indexOf(',') + 1));
        if(!(topLeftLat == 0d && topLeftLng == 0d)) {
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LAT_KEY, topLeftLat);
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LNG_KEY, topLeftLng);
        } else if(!(bottomRightLat == 0d && bottomRightLng == 0d)) {
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LAT_KEY, bottomRightLat);
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LNG_KEY, bottomRightLng);
        }
        intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_FLOOR_NAME_KEY, nameEditText.getText().toString());
        final Location location = DatabaseHelper.getLocation(new DatabaseOpenHelper(this).getReadableDatabase(), locationEditText.getText().toString());
        if(location != null) intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_LOCATION_NAME_KEY, location.getName());
        startActivityForResult(intent, REQUEST_CODE_EDIT_TOP_LEFT);
    }

    public void editBottomRight(View view) {
        final Intent intent = new Intent(this, ActivitySelectCoordinates.class);
        final String bottomRightS = bottomRightEditText.getText().toString();
        double bottomRightLat = Double.parseDouble(bottomRightS.substring(0, bottomRightS.indexOf(',')));
        double bottomRightLng = Double.parseDouble(bottomRightS.substring(bottomRightS.indexOf(',') + 1));
        final String topLeftS = topLeftEditText.getText().toString();
        double topLeftLat = Double.parseDouble(topLeftS.substring(0, topLeftS.indexOf(',')));
        double topLeftLng = Double.parseDouble(topLeftS.substring(topLeftS.indexOf(',') + 1));
        if(!(bottomRightLat == 0d && bottomRightLng == 0d)) {
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LAT_KEY, bottomRightLat);
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LNG_KEY, bottomRightLng);
        } else if(!(topLeftLat == 0d && topLeftLng == 0d)) {
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LAT_KEY, topLeftLat);
            intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_INITIAL_LNG_KEY, topLeftLng);
        }
        intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_FLOOR_NAME_KEY, nameEditText.getText().toString());
        final Location location = DatabaseHelper.getLocation(new DatabaseOpenHelper(this).getReadableDatabase(), locationEditText.getText().toString());
        if(location != null) intent.putExtra(ActivitySelectCoordinates.INTENT_EXTRA_LOCATION_NAME_KEY, location.getName());
        startActivityForResult(intent, REQUEST_CODE_EDIT_BOTTOM_RIGHT);
    }

    public void editImage(View view) {
        final Intent intent = new Intent(this, ActivitySelectImage.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    public void cancel(View view) {
        finish();
    }

    public void save(View view) {
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();

        final String topLeftS = topLeftEditText.getText().toString();
        float topLeftLat = Float.parseFloat(topLeftS.substring(0, topLeftS.indexOf(',')));
        float topLeftLng = Float.parseFloat(topLeftS.substring(topLeftS.indexOf(',') + 1));

        final String bottomRightS = bottomRightEditText.getText().toString();
        float bottomRightLat = Float.parseFloat(bottomRightS.substring(0, bottomRightS.indexOf(',')));
        float bottomRightLng = Float.parseFloat(bottomRightS.substring(bottomRightS.indexOf(',') + 1));

        int seq = Integer.parseInt(seqEditText.getText().toString());

        final Floor floor = new Floor(uuidEditText.getText().toString(), nameEditText.getText().toString(), seq, locationEditText.getText().toString(),
                imageUuidEditText.getText().toString(), topLeftLat, topLeftLng, bottomRightLat, bottomRightLng);

        DatabaseHelper.addFloor(sqLiteDatabase, floor);
        Log.d(TAG, "Added new floor: " + floor.toFullString());
        sqLiteDatabase.close();
        finish();
    }
}