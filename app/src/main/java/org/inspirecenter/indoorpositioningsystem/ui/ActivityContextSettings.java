package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.inspirecenter.indoorpositioningsystem.Installation;
import org.inspirecenter.indoorpositioningsystem.R;

import java.util.Locale;

public class ActivityContextSettings extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "ips";

    private GoogleApiClient mGoogleApiClient;

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        settingsFragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Google Play service connected OK.", Toast.LENGTH_SHORT).show();
        settingsFragment.userActivityCheckBoxPreference.setEnabled(true);

        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get the current activity.");
                            return;
                        }
                        ActivityRecognitionResult activityRecognitionResult = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = activityRecognitionResult.getMostProbableActivity();
                        Log.i(TAG, probableActivity.toString());
                        settingsFragment.userActivityCheckBoxPreference.setSummary(getString(R.string.user_activity_summary) + ": " + probableActivity);
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Google Play service suspended.", Toast.LENGTH_SHORT).show();
        settingsFragment.userActivityCheckBoxPreference.setEnabled(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play service connection failed: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        settingsFragment.userActivityCheckBoxPreference.setEnabled(false);
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

    public static class SettingsFragment extends PreferenceFragment
    {
        public static final IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        CheckBoxPreference deviceInstallationUUIDCheckBoxPreference;
        CheckBoxPreference deviceBrandCheckBoxPreference;
        CheckBoxPreference batteryLevelCheckBoxPreference;
        CheckBoxPreference chargingStateCheckBoxPreference;

        CheckBoxPreference connectedWifiMacAddressCheckBoxPreference;
        CheckBoxPreference connectedWifiSignalStrengthCheckBoxPreference;

        CheckBoxPreference userActivityCheckBoxPreference;

        CheckBoxPreference lightCheckBoxPreference;
        CheckBoxPreference temperatureCheckBoxPreference;
        CheckBoxPreference pressureCheckBoxPreference;
        CheckBoxPreference humidityCheckBoxPreference;

        CheckBoxPreference accelerometerCheckBoxPreference;
        CheckBoxPreference magnetometerCheckBoxPreference;
        CheckBoxPreference gravityCheckBoxPreference;
        CheckBoxPreference gyroscopeCheckBoxPreference;
        CheckBoxPreference rotationVectorCheckBoxPreference;

        private SensorManager sensorManager;
        private final ContextReceiver contextReceiver;
        private final SensorEventListener sensorEventListener;

        private WifiManager wifiManager;

        private SharedPreferences sharedPreferences;

        public SettingsFragment() {
            this.contextReceiver = new ContextReceiver();
            this.sensorEventListener = new ContextSensorEventListener();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.context_settings);

            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        @Override
        public void onResume() {
            super.onResume();

            {
                deviceInstallationUUIDCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.INSTALLATION_UUID.getName());
                deviceInstallationUUIDCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.INSTALLATION_UUID.getName(), true));
                deviceInstallationUUIDCheckBoxPreference.setSummary("Installation UUID: " + Installation.id(getActivity()));
            }

            {
                deviceBrandCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.DEVICE_MANUFACTURER.getName());
                deviceBrandCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.DEVICE_MANUFACTURER.getName(), true));
                deviceBrandCheckBoxPreference.setSummary("Device manufacturer: " + Build.MANUFACTURER);
            }

            {
                deviceBrandCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.DEVICE_MODEL.getName());
                deviceBrandCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.DEVICE_MODEL.getName(), true));
                deviceBrandCheckBoxPreference.setSummary("Device model: " + Build.MODEL);
            }

            {
                batteryLevelCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.BATTERY_LEVEL.getName());
                batteryLevelCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.BATTERY_LEVEL.getName(), true));

                chargingStateCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.CHARGING_STATE.getName());
                chargingStateCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.CHARGING_STATE.getName(), true));

                final Intent batteryStatus = getActivity().registerReceiver(contextReceiver, batteryIntentFilter);
                handleBatteryIntent(batteryStatus);
            }

            {
                final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String accessPointMacAddress = wifiInfo == null ? "not connected" : wifiInfo.getBSSID();

                connectedWifiMacAddressCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.CONNECTED_WIFI_MAC_ADDRESS.getName());
                connectedWifiMacAddressCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.CONNECTED_WIFI_MAC_ADDRESS.getName(), true));
                connectedWifiMacAddressCheckBoxPreference.setSummary("MAC address of connected Access Point: " + accessPointMacAddress);


                connectedWifiSignalStrengthCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.CONNECTED_WIFI_SIGNAL_STRENGTH.getName());
                connectedWifiSignalStrengthCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.CONNECTED_WIFI_SIGNAL_STRENGTH.getName(), true));
            }

            {
                userActivityCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.USER_ACTIVITY.getName());
                userActivityCheckBoxPreference.setChecked((sharedPreferences.getBoolean(ContextType.USER_ACTIVITY.getName(), true)));
            }

            {
                lightCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.LUMINOSITY.getName());
//                lightCheckBoxPreference.setChecked(sharedPreferences.getBoolean("light", true));

                final Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                if(lightSensor == null) {
                    lightCheckBoxPreference.setSummary("No light sensor installed");
                    lightCheckBoxPreference.setChecked(false);
                    lightCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                temperatureCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.AMBIENT_TEMPERATURE.getName());
                temperatureCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.AMBIENT_TEMPERATURE.getName(), true));

                final Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
                if(temperatureSensor == null) {
                    temperatureCheckBoxPreference.setSummary("No ambient temperature sensor installed");
                    temperatureCheckBoxPreference.setChecked(false);
                    temperatureCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                pressureCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.AMBIENT_AIR_PRESSURE.getName());
                pressureCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.AMBIENT_AIR_PRESSURE.getName(), true));

                final Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                if(pressureSensor == null) {
                    pressureCheckBoxPreference.setSummary("No pressure sensor installed");
                    pressureCheckBoxPreference.setChecked(false);
                    pressureCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                humidityCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.RELATIVE_HUMIDITY.getName());
                humidityCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.RELATIVE_HUMIDITY.getName(), true));

                final Sensor humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
                if(humiditySensor == null) {
                    humidityCheckBoxPreference.setSummary("No relative humidity sensor installed");
                    humidityCheckBoxPreference.setChecked(false);
                    humidityCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                accelerometerCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.ACCELERATION.getName());
                accelerometerCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.ACCELERATION.getName(), true));

                final Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if(accelerometerSensor == null) {
                    accelerometerCheckBoxPreference.setSummary("No accelerometer sensor installed");
                    accelerometerCheckBoxPreference.setChecked(false);
                    accelerometerCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                magnetometerCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.MAGNETIC_FIELD.getName());
                magnetometerCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.MAGNETIC_FIELD.getName(), true));

                final Sensor magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                if(magnetometerSensor == null) {
                    magnetometerCheckBoxPreference.setSummary("No magnetometer sensor installed");
                    magnetometerCheckBoxPreference.setChecked(false);
                    magnetometerCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                gravityCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.GRAVITY.getName());
                gravityCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.GRAVITY.getName(), true));

                final Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                if(gravitySensor == null) {
                    gravityCheckBoxPreference.setSummary("No gravity sensor installed");
                    gravityCheckBoxPreference.setChecked(false);
                    gravityCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                gyroscopeCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.GYROSCOPE.getName());
                gyroscopeCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.GYROSCOPE.getName(), true));

                final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                if(gyroscopeSensor == null) {
                    gyroscopeCheckBoxPreference.setSummary("No gyroscope sensor installed");
                    gyroscopeCheckBoxPreference.setChecked(false);
                    gyroscopeCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            {
                rotationVectorCheckBoxPreference = (CheckBoxPreference) findPreference(ContextType.ROTATION_VECTOR.getName());
                rotationVectorCheckBoxPreference.setChecked(sharedPreferences.getBoolean(ContextType.ROTATION_VECTOR.getName(), true));

                final Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                if(rotationVectorSensor == null) {
                    rotationVectorCheckBoxPreference.setSummary("No rotation vector sensor installed");
                    rotationVectorCheckBoxPreference.setChecked(false);
                    rotationVectorCheckBoxPreference.setEnabled(false);
                } else {
                    sensorManager.registerListener(sensorEventListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }

        @Override
        public void onPause() {
            super.onPause();

            getActivity().unregisterReceiver(contextReceiver);
        }

        public class ContextReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    handleBatteryIntent(intent);
                }
            }
        }

        public class ContextSensorEventListener implements SensorEventListener {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_LIGHT:
                        handleLightSensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_AMBIENT_TEMPERATURE:
                        handleTemperatureSensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_PRESSURE:
                        handlePressureSensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_RELATIVE_HUMIDITY:
                        handleHumiditySensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_ACCELEROMETER:
                        handleAccelerometerSensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        handleMagneticFieldSensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_GRAVITY:
                        handleGravitySensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_GYROSCOPE:
                        handleGyroscopeSensorEvent(sensorEvent);
                        return;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        handleRotationVectorSensorEvent(sensorEvent);
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

        private void handleBatteryIntent(final Intent batteryStatusIntent) {
            int level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = 100f * level / scale;
            int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            batteryLevelCheckBoxPreference.setSummary("Percentage of battery remaining: " + String.format(Locale.US, "%.1f", batteryPct) + "%");

            boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || isFull;
            int chargePlug = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            final String chargingState = isCharging ? ("charging " + (isFull ? "(full)" : "") + (usbCharge ? " via usb charge" : "") + (acCharge ? " via ac charge" : "")) : "discharging";

            chargingStateCheckBoxPreference.setSummary("Battery sensor: " + chargingState);
        }

        private void handleLightSensorEvent(final SensorEvent sensorEvent) {
            final float currentReading = sensorEvent.values[0];
            lightCheckBoxPreference.setSummary("Light sensor: " + currentReading + " lx (of max " + sensorEvent.sensor.getMaximumRange() + " lx)");
        }

        private void handleTemperatureSensorEvent(final SensorEvent sensorEvent) {
            final float currentReading = sensorEvent.values[0];
            temperatureCheckBoxPreference.setSummary("Temperature sensor: " + currentReading + " °C");
        }

        private void handlePressureSensorEvent(final SensorEvent sensorEvent) {
            final float currentReading = sensorEvent.values[0];
            pressureCheckBoxPreference.setSummary("Pressure sensor: " + currentReading + " hPa");
        }

        private void handleHumiditySensorEvent(final SensorEvent sensorEvent) {
            final float currentReading = sensorEvent.values[0];
            humidityCheckBoxPreference.setSummary("Humidity sensor: " + currentReading + " %");
        }

        private void handleAccelerometerSensorEvent(final SensorEvent sensorEvent) {
            String aX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
            String aY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
            String aZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
            accelerometerCheckBoxPreference.setSummary("Currently X: " + aX + ", Y: " + aY + ", Z: " + aZ);
        }

        private void handleMagneticFieldSensorEvent(final SensorEvent sensorEvent) {
            String mfX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
            String mfY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
            String mfZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
            magnetometerCheckBoxPreference.setSummary("Currently X: " + mfX + ", Y: " + mfY + ", Z: " + mfZ);
        }

        private void handleGravitySensorEvent(final SensorEvent sensorEvent) {
            String mfX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
            String mfY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
            String mfZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
            gravityCheckBoxPreference.setSummary("Currently X: " + mfX + ", Y: " + mfY + ", Z: " + mfZ);
        }

        private void handleGyroscopeSensorEvent(final SensorEvent sensorEvent) {
            String mfX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
            String mfY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
            String mfZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
            gyroscopeCheckBoxPreference.setSummary("Currently X: " + mfX + ", Y: " + mfY + ", Z: " + mfZ);
        }

        private void handleRotationVectorSensorEvent(final SensorEvent sensorEvent) {
            String mfX = String.format(Locale.US, "%.2f", sensorEvent.values[0]);
            String mfY = String.format(Locale.US, "%.2f", sensorEvent.values[1]);
            String mfZ = String.format(Locale.US, "%.2f", sensorEvent.values[2]);
            rotationVectorCheckBoxPreference.setSummary("Currently X: " + mfX + ", Y: " + mfY + ", Z: " + mfZ);
        }
    }
}