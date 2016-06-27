package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.json.JSONException;

import java.util.HashMap;

/**
 * @author Nearchos Paspallis
 * Created on 16/06/2014.
 */
public class ActivitySelectLocation extends Activity
{
    public static final String TAG = "ips";

    private ListView listView;

    private Location [] locations;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_select_location);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(R.string.Select_location);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView) findViewById(R.id.activity_select_location_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Location location = locations[position];
                Toast.makeText(ActivitySelectLocation.this, location.getName(), Toast.LENGTH_SHORT).show();

                if(ActivitySelectFloor.class.equals(nextActivityClass))
                {
                    final Intent selectFloorIntent = new Intent(ActivitySelectLocation.this, ActivitySelectFloor.class);
                    selectFloorIntent.putExtra("menu_location", location);
                    startActivity(selectFloorIntent);
                }
                else if(ActivityIndoorsLocation.class.equals(nextActivityClass))
                {
                    final Intent indoorsLocationIntent = new Intent(ActivitySelectLocation.this, ActivityIndoorsLocation.class);
                    indoorsLocationIntent.putExtra("locationUUID", location.getUuid());
                    startActivity(indoorsLocationIntent);
                }
                else
                {
                    Log.e(TAG, "Undefined nextActivityClass");
                    Toast.makeText(ActivitySelectLocation.this, "Undefined nextActivityClass", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private Class nextActivityClass;

    @Override
    protected void onResume()
    {
        super.onResume();

        nextActivityClass = (Class) getIntent().getSerializableExtra("nextActivity");

//        refresh();
        // todo fetch rom local DB
    }

//    private void refresh()
//    {
//        registerReceiver(progressReceiver, intentFilter);
//        final Intent getLocationsIntent = new Intent(this, FingerprintingSyncService.class);
//        getLocationsIntent.setAction(FingerprintingSyncService.ACTION_GET_LOCATIONS);
//        getLocationsIntent.putExtra(SyncService.EXTRA_PARAMETERS, new HashMap<String, String>());
//        setProgressBarIndeterminateVisibility(true);
//        startService(getLocationsIntent);
//    }
//
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
//                    menu_locations = FingerprintingJsonParser.parseGetLocations(payload);
//                    // update the UI
//                    listView.setAdapter(new LocationsAdapter(ActivitySelectLocation.this, menu_locations));
//                }
//                catch (JsonParseException jsonpe)
//                {
//                    Log.e(TAG, jsonpe.getMessage());
//                    final DialogError dialogError = new DialogError(context, jsonpe.getMessage());
//                    dialogError.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override public void onDismiss(DialogInterface dialog) {
//                            finish();
//                        }
//                    });
//                    dialogError.show();
//                }
//                catch (JSONException jsone)
//                {
//                    Log.e(TAG, jsone.getMessage());
//                    final DialogError dialogError = new DialogError(context, jsone.getMessage());
//                    dialogError.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override public void onDismiss(DialogInterface dialog) {
//                            finish();
//                        }
//                    });
//                    dialogError.show();
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