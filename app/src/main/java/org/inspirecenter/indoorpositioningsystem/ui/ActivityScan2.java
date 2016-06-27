package org.inspirecenter.indoorpositioningsystem.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.FingerprintElement;

import java.util.ArrayList;
import java.util.List;

public class ActivityScan2 extends AppCompatActivity {

    private Spinner startScanSpinner;
    private Button startScanButton;
    private ListView scanResultsListView;
    private ListView contextListView;

    private WifiManager wifiManager;

    private ScanResultsNotifier scanResultsNotifier;
    public static final IntentFilter scanResultsNotifierIntentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan2);

        scanResultsNotifier = new ScanResultsNotifier();

        startScanSpinner = (Spinner) findViewById(R.id.button_scan_spinner);
        startScanButton = (Button) findViewById(R.id.button_scan);
        scanResultsListView = (ListView) findViewById(R.id.list_view_scan_results);
        contextListView = (ListView) findViewById(R.id.list_view_context);

        final String [] scanOptions = getResources().getStringArray(R.array.scan_options);
        startScanSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scanOptions));

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    }

    public static final int PERMISSIONS_REQUEST_CODE_ACCESS_AND_CHANGE_WIFI = 42;

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void scan(View view) {
        if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            Toast.makeText(this, "WiFi is not enabled!", Toast.LENGTH_SHORT).show();
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[] { Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSIONS_REQUEST_CODE_ACCESS_AND_CHANGE_WIFI);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            scan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_AND_CHANGE_WIFI) { // todo and permission was actually granted
            scan();
        }
    }

    private void scan() {
        startScanButton.setEnabled(false);
        registerReceiver(scanResultsNotifier, scanResultsNotifierIntentFilter);
        boolean result = wifiManager.startScan();
        if(result) {
            Toast.makeText(this, "Scan started successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Scan failed to initiate", Toast.LENGTH_SHORT).show();
        }
    }

    public class ScanResultsNotifier extends BroadcastReceiver {

        @Override public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                unregisterReceiver(ScanResultsNotifier.this);
                setProgressBarIndeterminateVisibility(false);
                startScanButton.setEnabled(true);
                final List<ScanResult> scanResultList = wifiManager.getScanResults();
                final List<FingerprintElement> fingerprintElements = new ArrayList<>();
                for(final ScanResult scanResult : scanResultList) {
                    fingerprintElements.add(new FingerprintElement(scanResult.BSSID, scanResult.level, scanResult.frequency));
                }

                final FingerprintElementAdapter fingerprintElementAdapter = new FingerprintElementAdapter(context, fingerprintElements);
                scanResultsListView.setAdapter(fingerprintElementAdapter);
            }
        }
    }
}