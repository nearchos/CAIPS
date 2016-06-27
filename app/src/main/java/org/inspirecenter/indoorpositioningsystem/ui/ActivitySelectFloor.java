package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Location;

/**
 * @author Nearchos Paspallis
 * Created on 17/06/2014.
 */
public class ActivitySelectFloor extends Activity
{
    public static final String TAG = "org.codecyprus.android_client.ui.ActivitySelectFloor";

//    private final IntentFilter intentFilter = new IntentFilter(FingerprintingSyncService.ACTION_GET_LOCATION_COMPLETED);
//    private ProgressReceiver progressReceiver;

    private ListView listView;

    private Floor[] floors;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_select_floor);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(R.string.Select_floor);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView) findViewById(R.id.activity_select_floor_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Floor floor = floors[position];
                Toast.makeText(ActivitySelectFloor.this, floor.getName(), Toast.LENGTH_SHORT).show();

                final Intent startTrainingIntent = new Intent(ActivitySelectFloor.this, ActivitySubmitTraining.class);
                startTrainingIntent.putExtra("menu_location", location);
                startTrainingIntent.putExtra("floor", floor);
                startActivity(startTrainingIntent);
            }
        });

//        progressReceiver = new ProgressReceiver();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        location = (Location) getIntent().getSerializableExtra("menu_location");

        // todo get floors from db
//        registerReceiver(progressReceiver, intentFilter);
//        final Intent getLocationIntent = new Intent(this, FingerprintingSyncService.class);
//        getLocationIntent.setAction(FingerprintingSyncService.ACTION_GET_LOCATION);
//        final HashMap<String,String> parameters = new HashMap<String, String>();
//        parameters.put("uuid", menu_location.getUuid());
//        getLocationIntent.putExtra(SyncService.EXTRA_PARAMETERS, parameters);
//        setProgressBarIndeterminateVisibility(true);
//        startService(getLocationIntent);
    }

    private Location location;

//    private class ProgressReceiver extends BroadcastReceiver
//    {
//        @Override public void onReceive(final Context context, final Intent intent)
//        {
//            unregisterReceiver(this);
//            final String payload = (String) intent.getSerializableExtra(SyncService.EXTRA_PAYLOAD);
//            setProgressBarIndeterminateVisibility(false);
//
//            if(payload != null)
//            {
//                try
//                {
//                    menu_location = FingerprintingJsonParser.parseGetLocation(payload);
//                    floors = FingerprintingJsonParser.parseGetLocationFloors(payload, menu_location.getUuid());
//
//                    // update the UI
//                    listView.setAdapter(new FloorsAdapter(ActivitySelectFloor.this, floors));
//                }
//                catch (JsonParseException jsonpe)
//                {
//                    Log.e(TAG, jsonpe.getMessage());
//                    new DialogError(context, jsonpe.getMessage()).show();
//                }
//                catch (JSONException jsone)
//                {
//                    Log.e(TAG, jsone.getMessage());
//                    new DialogError(context, jsone.getMessage()).show();
//                }
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}