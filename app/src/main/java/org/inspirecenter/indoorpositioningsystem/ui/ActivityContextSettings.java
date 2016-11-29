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
import android.os.BatteryManager;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.inspirecenter.indoorpositioningsystem.R;

import java.util.Locale;

public class ActivityContextSettings extends AppCompatActivity {

    public static final String TAG = "ips";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
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

        CheckBoxPreference deviceBrandCheckBoxPreference;
        CheckBoxPreference batteryLevelCheckBoxPreference;
        CheckBoxPreference chargingStateCheckBoxPreference;

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
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        @Override
        public void onResume() {
            super.onResume();

            {
                deviceBrandCheckBoxPreference = (CheckBoxPreference) findPreference("device_brand");
                deviceBrandCheckBoxPreference.setChecked(sharedPreferences.getBoolean("device_brand", true));
                deviceBrandCheckBoxPreference.setSummary("Manufacturer: " + Build.MANUFACTURER + ", Model: " + Build.MODEL);
            }

            {
                batteryLevelCheckBoxPreference = (CheckBoxPreference) findPreference("battery_level");
                batteryLevelCheckBoxPreference.setChecked(sharedPreferences.getBoolean("battery_level", true));

                chargingStateCheckBoxPreference = (CheckBoxPreference) findPreference("charging_state");
                chargingStateCheckBoxPreference.setChecked(sharedPreferences.getBoolean("charging_state", true));

                final Intent batteryStatus = getActivity().registerReceiver(contextReceiver, batteryIntentFilter);
                handleBatteryIntent(batteryStatus);
            }

            {
                lightCheckBoxPreference = (CheckBoxPreference) findPreference("light");
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
                temperatureCheckBoxPreference = (CheckBoxPreference) findPreference("temperature");
                temperatureCheckBoxPreference.setChecked(sharedPreferences.getBoolean("temperature", true));

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
                pressureCheckBoxPreference = (CheckBoxPreference) findPreference("pressure");
                pressureCheckBoxPreference.setChecked(sharedPreferences.getBoolean("pressure", true));

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
                humidityCheckBoxPreference = (CheckBoxPreference) findPreference("humidity");
                humidityCheckBoxPreference.setChecked(sharedPreferences.getBoolean("humidity", true));

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
                accelerometerCheckBoxPreference = (CheckBoxPreference) findPreference("acceleration");
                accelerometerCheckBoxPreference.setChecked(sharedPreferences.getBoolean("acceleration", true));

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
                magnetometerCheckBoxPreference = (CheckBoxPreference) findPreference("magnetic_field");
                magnetometerCheckBoxPreference.setChecked(sharedPreferences.getBoolean("magnetic_field", true));

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
                gravityCheckBoxPreference = (CheckBoxPreference) findPreference("gravity");
                gravityCheckBoxPreference.setChecked(sharedPreferences.getBoolean("gravity", true));

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
                gyroscopeCheckBoxPreference = (CheckBoxPreference) findPreference("gyroscope");
                gyroscopeCheckBoxPreference.setChecked(sharedPreferences.getBoolean("gyroscope", true));

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
                rotationVectorCheckBoxPreference = (CheckBoxPreference) findPreference("rotation_vector");
                rotationVectorCheckBoxPreference.setChecked(sharedPreferences.getBoolean("rotation_vector", true));

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
