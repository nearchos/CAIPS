package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;

import org.inspirecenter.indoorpositioningsystem.R;

public class ActivityScan extends AppCompatActivity implements SensorEventListener {

    private TrainingView trainingView;

    private WifiManager wifiManager;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_scan);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // initialize environment sensors
        //todo

        trainingView = (TrainingView) findViewById(R.id.activity_training_custom_map_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // todo select menu_location and floor
        // todo if none set, show menu and send back

//        trainingView.init(menu_location, floor);//todo

        // Register environment sensors when the activity resumes.
        //todo
    }

    @Override
    protected void onPause()
    {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // todo
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore for now
    }
}