package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.*;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;

import java.util.*;

/**
 * @author Nearchos Paspallis
 * Created on 23/06/2014.
 */
public class ActivityIndoorsLocation extends Activity implements ActionBar.TabListener
{
    public static final String TAG = "ips";

    private ScanResultsNotifier scanResultsNotifier;
    public static final IntentFilter scanResultsNotifierIntentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    private WifiManager wifiManager;

    private ActionBar actionBar;

    private IndoorsLocationView indoorsLocationView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_indoors_location);

        setTitle(R.string.Indoors_positioning);

        indoorsLocationView = (IndoorsLocationView) findViewById(R.id.activity_indoors_location_view);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        actionBar = getActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        //todo
//        getLocationProgressReceiver = new GetLocationProgressReceiver();
//        getTrainingsProgressReceiver = new GetTrainingsProgressReceiver();
//        scanResultsNotifier = new ScanResultsNotifier();
    }

    private Location location;
    private String locationUUID;

    @Override
    protected void onResume()
    {
        super.onResume();
        locationUUID = getIntent().getStringExtra("locationUUID");
    }

    private MenuItem scanMenuItem = null;
    private MenuItem showTrainingPointsMenuItem = null;
    private boolean showTrainingPoints = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        scanMenuItem =  menu.add(R.string.Scan);//todo .setIcon(R.drawable.ic_menu_compass);
        scanMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        SubMenu subMenu = menu.addSubMenu(R.string.Settings);
        showTrainingPointsMenuItem = subMenu.add(R.string.Show_training_points)
                .setCheckable(true)
                .setChecked(showTrainingPoints);

        // this is an unfortunate hack, needed because there is no guarantee that onResume will be called *after* the menu is created
        if(floorUUIDToFloors == null)
        {
            getLocationAndFloors();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if(getString(R.string.Scan).equals(item.getTitle())) {
            scan();
            return true;
        } else if(getString(R.string.Show_training_points).equals(item.getTitle())) {
            showTrainingPoints = !showTrainingPoints;
            showTrainingPointsMenuItem.setChecked(showTrainingPoints);
            indoorsLocationView.setShowTrainingPoints(showTrainingPoints);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getLocationAndFloors() {
        scanMenuItem.setEnabled(false);
        showTrainingPointsMenuItem.setEnabled(false);
        //todo replace with db
//        registerReceiver(getLocationProgressReceiver, getLocationIntentFilter);
//        final Intent getLocationIntent = new Intent(this, FingerprintingSyncService.class);
//        getLocationIntent.setAction(FingerprintingSyncService.ACTION_GET_LOCATION);
//        final HashMap<String,String> parameters = new HashMap<String, String>();
//        parameters.put("uuid", locationUUID);
//        getLocationIntent.putExtra(SyncService.EXTRA_PARAMETERS, parameters);
//        setProgressBarIndeterminateVisibility(true);
//        startService(getLocationIntent);
    }

    private Map<Training,Map<String,Double>> trainings = new HashMap<>(); // initially empty

    private void refresh()
    {
        scanMenuItem.setEnabled(false);
        showTrainingPointsMenuItem.setEnabled(false);
        //todo replace with db
//        registerReceiver(getTrainingsProgressReceiver, getTrainingsIntentFilter);
//        final Intent getTrainingsIntent = new Intent(this, FingerprintingSyncService.class);
//        getTrainingsIntent.setAction(FingerprintingSyncService.ACTION_GET_TRAININGS);
//        final HashMap<String,String> parameters = new HashMap<String, String>();
//        parameters.put("locationUUID", menu_location.getUuid());
//        getTrainingsIntent.putExtra(SyncService.EXTRA_PARAMETERS, parameters);
//        setProgressBarIndeterminateVisibility(true);
//        startService(getTrainingsIntent);
    }

    private void scan() {
        if(!wifiManager.isWifiEnabled())         {
            Toast.makeText(this, R.string.You_must_activate_the_WiFi_first, Toast.LENGTH_SHORT).show();
        } else {
            setProgressBarIndeterminateVisibility(true);
            scanMenuItem.setEnabled(false);
            showTrainingPointsMenuItem.setEnabled(false);
            registerReceiver(scanResultsNotifier, scanResultsNotifierIntentFilter);
            wifiManager.startScan();
        }
    }

    private Map<String,Floor> floorUUIDToFloors = null;

    //todo
    public class ScanResultsNotifier extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                unregisterReceiver(ScanResultsNotifier.this);
                setProgressBarIndeterminateVisibility(false);
                scanMenuItem.setEnabled(true);
                showTrainingPointsMenuItem.setEnabled(true);

                updateScoresAndLocation(wifiManager.getScanResults());
           }
        }
    }

    private double lat = 0d;
    private double lng = 0d;

    private final Map<Training,Double> allTrainingsToScores = new HashMap<>(); // initially empty

    private void updateScoresAndLocation(final List<ScanResult> scanResults) {
        if(trainings == null || trainings.isEmpty()) {
            Toast.makeText(this, "Empty training set", Toast.LENGTH_SHORT).show();
            return;
        }

        // compute scores
        allTrainingsToScores.clear();
        for(final Training training : trainings.keySet()) {
            allTrainingsToScores.put(training, getScore(trainings.get(training), scanResults));
        }

        // sort by score
        final Set<Map.Entry<Training,Double>> entries = allTrainingsToScores.entrySet();
        final List<Map.Entry<Training,Double>> entriesList = new ArrayList<Map.Entry<Training, Double>>(entries);
        Collections.sort(entriesList, new Comparator<Map.Entry<Training, Double>>() {
            @Override public int compare(Map.Entry<Training, Double> lhs, Map.Entry<Training, Double> rhs) {
                return lhs.getValue().compareTo(rhs.getValue());
            }
        });

        Log.d(TAG, "entriesList: " + entriesList);
        final Training selectedTraining = entriesList.get(entriesList.size()-1).getKey(); // get last item
        final double selectedScore = entriesList.get(entriesList.size()-1).getValue(); // get last item value

        // select floor/tab
        final Floor currentlySelectedFloor = (Floor) actionBar.getSelectedTab().getTag();
        final Floor newlySelectedFloor = floorUUIDToFloors.get(selectedTraining.getFloorUuid());
        if(!currentlySelectedFloor.getUuid().equals(newlySelectedFloor.getUuid()))
        {
            indoorsLocationView.init(location, newlySelectedFloor);
            for(int i = 0; i < actionBar.getTabCount(); i++)
            {
                final ActionBar.Tab tab = actionBar.getTabAt(i);
                if(newlySelectedFloor.equals(tab.getTag()))
                {
                    actionBar.selectTab(tab);
                    break;
                }
            }
        }

        // update the UI
        indoorsLocationView.setTrainingPoints(getFloorTrainingsToScores(currentlySelectedFloor));

        // select coordinates
        this.lat = selectedTraining.getLat();
        this.lng = selectedTraining.getLng();
        indoorsLocationView.setSelectedCoordinates(lat, lng);
    }

    /**
     * Computes a score between the suggested fingerprint (ssidMeasurements) and the scan results. The score will be in
     * the range 0 to 1, with 0 meaning very poor match and 1 meaning perfect match.
     */
    private double getScore(final Map<String,Double> ssidMeasurements, final List<ScanResult> scanResults)
    {
        // initially, assume all fingerprints have not been checked
        final Set<String> uncheckedSsidMeasurements = new HashSet<String>();
        uncheckedSsidMeasurements.addAll(ssidMeasurements.keySet());

        double sum = 0d;
        for(final ScanResult scanResult : scanResults)
        {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scanResult.level + 100d) / 100d;
            final double measuredLevel;
            if(ssidMeasurements.containsKey(scanResult.BSSID))
            {
                measuredLevel = (ssidMeasurements.get(scanResult.BSSID) + 100d) / 100d;
                uncheckedSsidMeasurements.remove(scanResult.BSSID);
            }
            else
            {
                measuredLevel = 0d;
            }
            sum += Math.pow(scannedLevel - measuredLevel, 2d);
        }

        // now, account for all unchecked fingerprints
        for(final String uncheckedSsidMeasurement : uncheckedSsidMeasurements)
        {
            sum += Math.pow((ssidMeasurements.get(uncheckedSsidMeasurement) + 100d)/100d, 2d);
        }

        final int maxSize = Math.max(scanResults.size(), ssidMeasurements.size());

        return 1 - Math.sqrt(sum / maxSize);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        final Floor floor = (Floor) tab.getTag();
        indoorsLocationView.init(location, floor);
        indoorsLocationView.setTrainingPoints(getFloorTrainingsToScores(floor));
        indoorsLocationView.setSelectedCoordinates(lat, lng);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // nothing
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // nothing
    }

    private Map<Training,Double> getFloorTrainingsToScores(final Floor floor) {
        final Map<Training,Double> floorTrainingsToScores = new HashMap<Training, Double>();
        for(final Map.Entry<Training,Double> trainingToScore : allTrainingsToScores.entrySet())
        {
            if(trainingToScore.getKey().getFloorUuid().equals(floor.getUuid()))
            {
                floorTrainingsToScores.put(trainingToScore.getKey(), trainingToScore.getValue());
            }
        }
        return floorTrainingsToScores;
    }
}