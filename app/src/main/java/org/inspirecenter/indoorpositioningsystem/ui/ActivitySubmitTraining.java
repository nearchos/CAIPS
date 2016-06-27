package org.inspirecenter.indoorpositioningsystem.ui;

import android.Manifest;
import android.app.ActionBar;
import android.content.*;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.util.*;

/**
 * @author Nearchos Paspallis
 * Created on 16/06/2014.
 */
public class ActivitySubmitTraining extends AppCompatActivity
//        implements SensorEventListener
{
    public static final String TAG = "ips";

    private Spinner floorSpinner;
    private Spinner autoSubmitSpinner;
    private Button submitButton;
    private TrainingView trainingView;

    private ScanResultsNotifier scanResultsNotifier;
    private ContextSensorEventListener contextSensorEventListener;
    public static final IntentFilter scanResultsAvailableIntentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    private WifiManager wifiManager;

    private SensorManager sensorManager;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_submit_training);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        floorSpinner = (Spinner) findViewById(R.id.activity_training_floor_spinner);
        autoSubmitSpinner = (Spinner) findViewById(R.id.activity_training_auto_submit_spinner);
        submitButton = (Button) findViewById(R.id.activity_training_submit_button);
        trainingView = (TrainingView) findViewById(R.id.activity_training_custom_map_view);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.scan_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoSubmitSpinner.setAdapter(adapter);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        // initialize environment sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        scanResultsNotifier = new ScanResultsNotifier();
        contextSensorEventListener = new ContextSensorEventListener();
    }

    private String locationUuid;
    private String selectedFloorUuid;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_submit_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_submit_training_context_settings:
                startActivity(new Intent(this, ActivityContextSettings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static final int PERMISSIONS_REQUEST_CODE_ACCESS_AND_CHANGE_WIFI = 42;

    public void submit(View view) {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            submitButton.setText(R.string.Submit);
        } else if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            Toast.makeText(this, "WiFi is not enabled!", Toast.LENGTH_SHORT).show();
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            requestPermissions(new String[] { Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_AND_CHANGE_WIFI);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            trigger();
        }
    }

    private CountDownTimer countDownTimer = null;

    public void trigger() {
        final int [] intervals = getResources().getIntArray(R.array.scan_intervals_in_sec);
        final int interval = intervals[autoSubmitSpinner.getSelectedItemPosition()];
        if(interval > 0) {
            final long intervalInMs = interval * 1000L;
            countDownTimer = new CountDownTimer(intervalInMs, 1000) {
                public void onTick(long millisUntilFinished) {
                    submitButton.setText(String.format(Locale.US, "Scan in %d ms", millisUntilFinished / 1000));
                }

                public void onFinish() {
                    submitButton.setText(R.string.Submit);
                    scan();
                }
            }.start();
        } else {
            scan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_AND_CHANGE_WIFI) { // todo and permission was actually granted
            trigger();
        }
    }

    private void scan() {
        setProgressBarIndeterminateVisibility(true);
        submitButton.setEnabled(false);
        submitButton.setText(R.string.Scanning);
        registerReceiver(scanResultsNotifier, scanResultsAvailableIntentFilter);
        wifiManager.startScan();
    }

    public class ScanResultsNotifier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                unregisterReceiver(ScanResultsNotifier.this);
                setProgressBarIndeterminateVisibility(false);

                submitButton.setText(R.string.Scan);
                submitButton.setEnabled(true);
                final int [] intervals = getResources().getIntArray(R.array.scan_intervals_in_sec);
                final int interval = intervals[autoSubmitSpinner.getSelectedItemPosition()];
                if(interval > 0) {
                    trigger();
                }

                final double [] coordinates = trainingView.getSelectedCoordinates();

                final List<ScanResult> scanResults = wifiManager.getScanResults();
                final String radiomapAsJsonArray = getRadiomapAsJsonArray(scanResults);
                final String contextAsJsonArray = contextSensorEventListener.getContextAsJsonArray();
                final double lat = coordinates[0];
                final double lng = coordinates[1];
                Log.d(TAG, "selected lat: " + lat + ", lng: " + lng);

                final String createdBy = PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);

                final Training training = new Training(
                        UUID.randomUUID().toString(),
                        createdBy,
                        locationUuid,
                        selectedFloorUuid,
                        System.currentTimeMillis(),
                        radiomapAsJsonArray,
                        contextAsJsonArray,
                        lat,
                        lng
                );

                // store in db
                DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(context);
                DatabaseHelper.addTraining(databaseOpenHelper.getWritableDatabase(), training);
            }
        }
    }

//    public static final IntentFilter BATTERY_INTENT_FILTER = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private String getRadiomapAsJsonArray(final List<ScanResult> scanResults) {
        final StringBuilder contextStringBuilder = new StringBuilder("[\n");
        int num = 0;
        for(final ScanResult scanResult : scanResults) {
            contextStringBuilder.append("  { \"bssid\": \"").append(scanResult.BSSID)
                    .append("\", \"level\": ").append(scanResult.level)
                    .append(", \"frequency\": ").append(scanResult.frequency).append("}");
            contextStringBuilder.append(++num < scanResults.size() ? ",\n" : "\n");
        }
        contextStringBuilder.append("]\n");
        return contextStringBuilder.toString();
    }

//    /**
//     * TODO add sensing from http://developer.android.com/guide/topics/sensors/sensors_environment.html
//     * @return
//     */
//    private String getContextAsJsonArray() {
//        final Intent batteryStatus = registerReceiver(null, BATTERY_INTENT_FILTER);
//        if(batteryStatus == null)
//        {
//            return  "[" +
//                    (lightSensor != null ? "  \"illuminanceInLux\": " + illuminanceInLux + "," : "") +
//                    (pressureSensor != null ? "  \"ambientAirPressureInMillibar\": " + ambientAirPressureInMillibar + "," : "") +
//                    (relativeHumiditySensor != null ? "  \"relativeHumidityPercentage\": " + relativeHumidityPercentage + "," : "") +
//                    (ambientTemperatureSensor != null ? "  \"ambientAirTemperatureInCelsius\": " + ambientAirTemperatureInCelsius + "," : "") +
//                    "  \"model\": \"" + Build.MODEL + "\"," +
//                    "  \"product\": \"" + Build.PRODUCT+ "\"" +
//                    "]";
//        }
//        else
//        {
//            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
//            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//
//            return  "{" +
//                    (lightSensor != null ? "  \"illuminanceInLux\": " + illuminanceInLux + "," : "") +
//                    (pressureSensor != null ? "  \"ambientAirPressureInMillibar\": " + ambientAirPressureInMillibar + "," : "") +
//                    (relativeHumiditySensor != null ? "  \"relativeHumidityPercentage\": " + relativeHumidityPercentage + "," : "") +
//                    (ambientTemperatureSensor != null ? "  \"ambientAirTemperatureInCelsius\": " + ambientAirTemperatureInCelsius + "," : "") +
//
//                    "  \"batteryCharging\": " + isCharging + "," +
//                    "  \"batteryLevel\": " + level + "," +
//                    "  \"batteryScale\": " + scale + ", " +
//
//                    "  \"model\": \"" + Build.MODEL + "\"," +
//                    "  \"product\": \"" + Build.PRODUCT+ "\"" +
//                    "}";
//        }
//    }
//
//    float illuminanceInLux = 0f;
//    float ambientAirPressureInMillibar = 0f;
//    float relativeHumidityPercentage = 0f;
//    float ambientAirTemperatureInCelsius = 0f;

//    @Override
//    public final void onSensorChanged(SensorEvent event)
//    {
//        if(lightSensor == event.sensor)
//        {
//            illuminanceInLux = event.values[0];
//        }
//        else if(pressureSensor == event.sensor)
//        {
//            ambientAirPressureInMillibar = event.values[0];
//        }
//        else if(relativeHumiditySensor == event.sensor)
//        {
//            relativeHumidityPercentage = event.values[0];
//        }
//        else if(ambientTemperatureSensor == event.sensor)
//        {
//            ambientAirTemperatureInCelsius = event.values[0];
//        }
//
//        float millibars_of_pressure = event.values[0];
//        // Do something with this sensor data.
//    }

//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // ignore for now
//    }

//    public static final String INTENT_EXTRA_FLOOR_UUID_KEY = "floor_uuid";
    public static final String INTENT_EXTRA_LOCATION_UUID_KEY = "location_uuid";

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();
//        floorUUID = intent.getStringExtra(INTENT_EXTRA_FLOOR_UUID_KEY);
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
//        final Floor floor = DatabaseHelper.getFloor(databaseOpenHelper.getReadableDatabase(), floorUUID);
//
//        final Location location = DatabaseHelper.getLocation(databaseOpenHelper.getReadableDatabase(), floor.getLocationUUID());
//        locationUuid = location.getUuid();

        locationUuid = intent.getStringExtra(INTENT_EXTRA_LOCATION_UUID_KEY);
        final Location location = DatabaseHelper.getLocation(databaseOpenHelper.getReadableDatabase(), locationUuid);
        final Floor [] floors = DatabaseHelper.getFloors(databaseOpenHelper.getReadableDatabase(), locationUuid);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setTitle(getString(R.string.Training) + " - " + location.getName());

        floorSpinner.setAdapter(new ArrayAdapter<Floor>(this, android.R.layout.simple_list_item_1, floors));
        final Floor selectedFloor = floors[floorSpinner.getSelectedItemPosition()];
        selectedFloorUuid = selectedFloor.getUuid();
        trainingView.init(location, selectedFloor);
        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Floor selectedFloor = floors[position];
                selectedFloorUuid = selectedFloor.getUuid();
                trainingView.init(location, selectedFloor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Register environment sensors when the activity resumes.

        // register for battery updates
        registerReceiver(contextSensorEventListener, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // register for light updates
        final Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor != null) sensorManager.registerListener(contextSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for temperature updates
        final Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if(temperatureSensor != null) sensorManager.registerListener(contextSensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for pressure updates
        final Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pressureSensor != null) sensorManager.registerListener(contextSensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for humidity updates
        final Sensor humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if(humiditySensor != null) sensorManager.registerListener(contextSensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for accelerometer updates
        final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometerSensor != null) sensorManager.registerListener(contextSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for magnetometer updates
        final Sensor magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometerSensor != null) sensorManager.registerListener(contextSensorEventListener, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for gravity updates
        final Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if(gravitySensor != null) sensorManager.registerListener(contextSensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for gyroscope updates
        final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(gyroscopeSensor != null) sensorManager.registerListener(contextSensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // register for rotationVector updates
        final Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(rotationVectorSensor != null) sensorManager.registerListener(contextSensorEventListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        try { unregisterReceiver(scanResultsNotifier); } catch (IllegalArgumentException iae) { /* nothing */ }
        unregisterReceiver(contextSensorEventListener);
        sensorManager.unregisterListener(contextSensorEventListener);
    }

    public class ContextSensorEventListener extends BroadcastReceiver  implements SensorEventListener {

        final Map<String,String> latestValues = new HashMap<>();

        public ContextSensorEventListener() {
            super();
            // add constant context (make, model, etc.)
            latestValues.put("make", "[ \"" + Build.MANUFACTURER + "\" ]");
            latestValues.put("model", "[ \"" + Build.MODEL + "\" ]");
        }

        String getContextAsJsonArray() {
            final StringBuilder contextStringBuilder = new StringBuilder("[\n");
            int num = 0;
            for(final String key : latestValues.keySet()) {
                contextStringBuilder.append("  { \"").append(key).append("\": ")
                        .append(latestValues.get(key)).append(" }");
                contextStringBuilder.append(++num < latestValues.size() ? ",\n" : "\n");
            }

            contextStringBuilder.append("]\n");

            return contextStringBuilder.toString();
        }

        @Override
        public void onReceive(Context context, Intent batteryStatusIntent) {

            if(Intent.ACTION_BATTERY_CHANGED.equals(batteryStatusIntent.getAction())) {
                int level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = 100f * level / scale;
                int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                latestValues.put("battery-level", "[ \"" + batteryPct + "%\" ]");

                boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || isFull;
                int chargePlug = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                final String chargingState = isCharging ? ("charging " + (isFull ? "(full)" : "") + (usbCharge ? " via usb charge" : "") + (acCharge ? " via ac charge" : "")) : "discharging";

                latestValues.put("battery-charging-state", "[ \"" + chargingState + "\" ]");
            }
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_LIGHT:
                    latestValues.put("luminosity", "[ " + sensorEvent.values[0] + " ]");
                    return;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    latestValues.put("temperature", "[ " + sensorEvent.values[0] + " ]");
                    return;
                case Sensor.TYPE_PRESSURE:
                    latestValues.put("pressure", "[ " + sensorEvent.values[0] + " ]");
                    return;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    latestValues.put("humidity", "[ " + sensorEvent.values[0] + " ]");
                    return;
                case Sensor.TYPE_ACCELEROMETER:
                    String aX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
                    String aY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
                    String aZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
                    latestValues.put("acceleration", "[ " + aX + ", " + aY + ", " + aZ + " ]");
                    return;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    String mfX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
                    String mfY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
                    String mfZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
                    latestValues.put("magnetic-field", "[ " + mfX + ", " + mfY + ", " + mfZ + " ]");
                    return;
                case Sensor.TYPE_GRAVITY:
                    String gX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
                    String gY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
                    String gZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
                    latestValues.put("magnetic-field", "[ " + gX + ", " + gY + ", " + gZ + " ]");
                    return;
                case Sensor.TYPE_GYROSCOPE:
                    String gyX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
                    String gyY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
                    String gyZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
                    latestValues.put("gyroscope", "[ " + gyX + ", " + gyY + ", " + gyZ + " ]");
                    return;
                case Sensor.TYPE_ROTATION_VECTOR:
                    String rvX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
                    String rvY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
                    String rvZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
                    latestValues.put("rotation-vector", "[ " + rvX + ", " + rvY + ", " + rvZ + " ]");
                    return;
                default:
                    Log.e(TAG, "Unknown or unexpected sensor type: " + sensorEvent.sensor.getType());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing for now
        }
    }

}