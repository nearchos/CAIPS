package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;
import org.inspirecenter.indoorpositioningsystem.model.ContextEntry;
import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;
import org.inspirecenter.indoorpositioningsystem.model.Datasets;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.Message;
import org.inspirecenter.indoorpositioningsystem.model.RadioDataEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

public class ActivityDatasets extends AppCompatActivity {

    public static final String TAG = "caips";

    public static final String DATASET_KEY = "dataset";

    private ProgressBar progressBar;
    private ListView datasetsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datasets);

        this.progressBar = findViewById(R.id.activity_datasets_progress_bar);
        this.datasetsListView = findViewById(R.id.activity_datasets_list_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh(null);
    }

    public void refresh(View view) {
        new GetDatasetsAsyncTask().execute();
    }

    public void upload(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.Choose_method_of_upload)
                .setPositiveButton(R.string.Local_file, (dialog, which) -> uploadFromFile())
                .setNeutralButton(R.string.Database, (dialog, which) -> uploadFromDatabase())
                .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public static final int READ_REQUEST_CODE = 42;

    private void uploadFromFile() {
        // Fires an intent to spin up the "file chooser" UI and select an image.

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // To search for all documents available via installed storage providers, it would be "*/*".
        intent.setType("text/plain");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent, and
        // the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent. Instead, a URI to
            // that document will be contained in the return intent provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                assert uri != null;
                try {
                    final ContentResolver contentResolver = getContentResolver();
                    final InputStream inputStream = contentResolver.openInputStream(uri);
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    final StringBuilder jsonStringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonStringBuilder.append(line);
                    }
                    bufferedReader.close();

                    final Dataset dataset = getDataset(jsonStringBuilder.toString(), uri.getPath());
                    uploadDataset(dataset);
                } catch (IOException ioe) {
                    Log.e(TAG, "Error reading from local uri: " + uri.getPath(), ioe);
                    Toast.makeText(this, "Error reading from local uri: " + uri.getPath(), Toast.LENGTH_SHORT).show();
                } catch (JSONException jsone) {
                    Log.e(TAG, "Error parsing JSON file from local uri: " + uri.getPath(), jsone);
                    Toast.makeText(this, "Error parsing JSON file from local uri: " + uri.getPath(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadFromDatabase() {
        // pick up the data from the local DB and upload them to a new library

        // get locations
        final String username = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM menu_locations WHERE createdBy=?", new String [] { username });
        int numOfRows = cursor.getCount();
        cursor.moveToFirst();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnCreatedByIndex = cursor.getColumnIndex("createdBy");
        int columnTimestampIndex = cursor.getColumnIndex("timestamp");
        final Location[] locations = new Location[numOfRows];
        for(int i = 0; i < numOfRows; i++) {
            final Location location = new Location(
                    cursor.getString(columnUUIDIndex),
                    cursor.getString(columnNameIndex),
                    cursor.getString(columnCreatedByIndex),
                    cursor.getLong(columnTimestampIndex)
            );
            locations[i] = location;
            cursor.moveToNext();
        }
        cursor.close();

        final ListView locationsListView = new ListView(this);
        locationsListView.setPadding(16, 16, 16, 16);
        locationsListView.setAdapter(new LocationsAdapter(this, locations));
        locationsListView.setOnItemClickListener((parent, view1, position, id) -> {
            final Dataset dataset = getDataset(locations[position]);
            uploadDataset(dataset);
        });

        // show dialog with chooser among data on local DB, categorized by location
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Select_location)
                .setView(locationsListView)
                .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void uploadDataset(final Dataset dataset) {
        // show dialog with chooser among data on local DB, categorized by location
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(getString(R.string.Upload_dataset, dataset.getNumberOfMeasurements()))
                .setPositiveButton(R.string.Upload, (dialog, which) -> new PostDatasetsAsyncTask().execute(dataset))
                .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    class GetDatasetsAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            datasetsListView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... aVoid) {
            try {
                return doHttpGet("https://caips-server.appspot.com/api/json/datasets");
            } catch (IOException ioe) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            final Gson gson = new Gson();
            final Datasets datasets = gson.fromJson(json, Datasets.class);
            // add to list
            if(datasets.isEmpty()) Toast.makeText(ActivityDatasets.this, R.string.Empty_dataset, Toast.LENGTH_SHORT).show();
            final List<DatasetMetadata> datasetMetadata = datasets.getDatasetsMetadata();
            datasetsListView.setAdapter(new DatasetMetadataAdapter(ActivityDatasets.this, datasetMetadata));
            datasetsListView.setOnItemClickListener((parent, view, position, id) -> showDataset(datasetMetadata.get(position)));
            datasetsListView.setOnItemLongClickListener((parent, view, position, id) -> {
                askDeleteDataset(datasetMetadata.get(position));
                return true;
            });

            progressBar.setVisibility(View.GONE);
            datasetsListView.setVisibility(View.VISIBLE);
        }
    }

    private String doHttpGet(final String serviceUrl) throws IOException {
        InputStream inputStream = null; // must be declared here so it can be ’finally’ closed
        try {
            URL url = new URL(serviceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // this is the default HTTP method anyway
            connection.setDoInput(true); // connections can be used for input or output
            connection.connect(); // connects and starts the query
            int response = connection.getResponseCode(); // should be 200 if all is OK
            Log.d(TAG, "Response code: " + response);

            // handle response
            inputStream = connection.getInputStream(); // get the input stream to read data from the connection
            // a buffered reader automatically handles intermittent or long replies
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            // the string builder is used to collect the lines of the reply into a single string
            final StringBuilder stringBuilder = new StringBuilder();
            String line; // used as a temporary buffer
            while ((line = reader.readLine()) != null) { // when the input stream is exhausted, it returns ’null’
                stringBuilder.append(line).append("\n"); // append the read line and a ’new line’ character
                }
            return stringBuilder.toString(); // at this point we have collected the full reply and we terminate the method
        } finally {
            // makes sure that the ’InputStream’ is closed after we are done using it
            if (inputStream != null) { inputStream.close(); }
        }
    }

    private Dataset getDataset(final Location location) {

        // get fingerprints
        final String username = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final Training[] trainings = DatabaseHelper.getTrainingsByLocationUuid(databaseOpenHelper.getReadableDatabase(), location.getUuid());
        databaseOpenHelper.close();

        final List<MeasurementEntry> measurementEntries = new Vector<MeasurementEntry>();
        for(int i = 0; i < trainings.length; i++) {
            final Training training = trainings[i];
            measurementEntries.add(fromTraining(training));
        }

        return new Dataset(null, username, System.currentTimeMillis(), 1, location.getName() + " created by " + location.getCreatedBy(), measurementEntries);
    }

    private static MeasurementEntry fromTraining(final Training training) {
        return new MeasurementEntry(
                training.getUUID(),
                0L,
                training.getFloorUuid(),
                training.getCreatedBy(),
                training.getTimestamp(),
                new Coordinates(training.getLat(), training.getLng()),
                fromContextJson(training.getContextAsJSON()),
                fromRadioMapJson(training.getRadiomapAsJSON())
        );
    }

    private static List<ContextEntry> fromContextJson(final String json) {
        final Vector<ContextEntry> contextEntries = new Vector<>();
        // todo
        return contextEntries;
    }

    private static List<RadioDataEntry> fromRadioMapJson(final String json) {
        final List<RadioDataEntry> dataEntries = new Vector<>();
        // todo
        return dataEntries;
    }

    private Dataset getDataset(final String legacyJsonFormat, final String description) throws JSONException {

        final JSONArray jsonArray = new JSONArray(legacyJsonFormat);
        final int numOfMeasurementEntries = jsonArray.length();
        final List<MeasurementEntry> measurementEntries = new Vector<>();
        for(int i = 0; i < numOfMeasurementEntries; i++) {
            // radiomap": [
            final JSONObject jsonObject = jsonArray.getJSONObject(i);

            final Vector<ContextEntry> contextEntries = new Vector<>();
            // todo handle the problem with the instantiation of Serializable (generic) objects
//            final JSONArray contextJsonArray = jsonObject.getJSONArray("context");
//            final int numOfContextEntries = contextJsonArray.length();
//            for(int j = 0; j < numOfContextEntries; j++) {
//                final JSONObject contextEntryJsonObject = contextJsonArray.getJSONObject(j);
//                final JSONArray contextNames = contextEntryJsonObject.names();
//                final int numOfContextNames = contextNames.length();
//                for(int k = 0; k < numOfContextNames; k++) {
//                    final String name = contextNames.getString(k);
//                    final JSONArray valuesJsonArray = contextEntryJsonObject.getJSONArray(name);
//                    final int numOfValues = valuesJsonArray.length();
//                    final Serializable [] values = new Serializable[numOfValues];
//                    for(int l = 0; l < numOfValues; l++) {
//                        values[l] = (Serializable) valuesJsonArray.get(l);
//                    }
//
//                    contextEntries.add(new ContextEntry(name, values));
//                }
//            }

            final Vector<RadioDataEntry> radioDataEntries = new Vector<>();
            final JSONArray radioMapJsonArray = jsonObject.getJSONArray("radiomap");
            final int numOfRadioMapEntries = radioMapJsonArray.length();
            for(int j = 0; j < numOfRadioMapEntries; j++) {
                final JSONObject radioDataEntryJsonObject = radioMapJsonArray.getJSONObject(j);
                radioDataEntries.add(new RadioDataEntry(
                        radioDataEntryJsonObject.getString("bssid"),
                        radioDataEntryJsonObject.getInt("level"),
                        radioDataEntryJsonObject.getInt("frequency")
                ));
            }

            measurementEntries.add(new MeasurementEntry(
                    jsonObject.getString("uuid"),
                    0L,
                    jsonObject.getString("floorUuid"),
                    jsonObject.getString("createdBy"),
                    jsonObject.getLong("timestamp"),
                    new Coordinates(jsonObject.getDouble("lat"), jsonObject.getDouble("lng")),
                    contextEntries,
                    radioDataEntries
            ));
        }

        final String username = PreferenceManager.getDefaultSharedPreferences(this).getString(ActivityAuthenticate.KEY_ACCOUNT_NAME, null);
        return new Dataset(username, System.currentTimeMillis(), 1, description, measurementEntries);
    }

    class PostDatasetsAsyncTask extends AsyncTask<Dataset, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            datasetsListView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Dataset... datasets) {
            try {
                final String json = new Gson().toJson(datasets[0]);
                return doHttpPost("https://caips-server.appspot.com/api/json/create-dataset", json);
            } catch (IOException ioe) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            final Gson gson = new Gson();
            final Message message = gson.fromJson(json, Message.class);
            // add to list
            if(message.getType() == Message.Type.ERROR) {
                final int numOfMessages = message.getNumberOfMessages();
                String errorMessage = "";
                for(int i = 0; i < numOfMessages; i++) {
                    Log.w(TAG, message.getMessage(i));
                    errorMessage += message.getMessage(i);
                }
                Toast.makeText(ActivityDatasets.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
            datasetsListView.setVisibility(View.VISIBLE);
        }
    }

    private String doHttpPost(final String serviceUrl, final String json) throws IOException {
        InputStream inputStream = null; // must be declared here so it can be ’finally’ closed
        OutputStream outputStream = null; // must be declared here so it can be ’finally’ closed
        try {
            URL url = new URL(serviceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST"); // this is the default HTTP method anyway
            connection.setDoOutput(true); // connections can be used for input or output
            connection.connect(); // connects and starts the query

            outputStream = new BufferedOutputStream(connection.getOutputStream());
            final BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(json);
            writer.flush();

            int response = connection.getResponseCode(); // should be 200 if all is OK
            Log.d(TAG, "Response code: " + response);

            // handle response
            inputStream = connection.getInputStream(); // get the input stream to read data from the connection
            // a buffered reader automatically handles intermittent or long replies
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            // the string builder is used to collect the lines of the reply into a single string
            final StringBuilder stringBuilder = new StringBuilder();
            String line; // used as a temporary buffer
            while ((line = reader.readLine()) != null) { // when the input stream is exhausted, it returns ’null’
                stringBuilder.append(line).append("\n"); // append the read line and a ’new line’ character
            }
            return stringBuilder.toString(); // at this point we have collected the full reply and we terminate the method
        } finally {
            // makes sure that the ’InputStream’ is closed after we are done using it
            if (inputStream != null) { inputStream.close(); }
            if (outputStream != null) { outputStream.close(); }
        }
    }

    private void showDataset(final DatasetMetadata datasetMetadata) {
        final Intent intent = new Intent(this, ActivitySimulation.class);
        intent.putExtra(ActivitySimulation.EXTRA_KEY_DATASET_METADATA, datasetMetadata);
        startActivity(intent);
    }

    private void askDeleteDataset(final DatasetMetadata datasetMetadata) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.Delete_dataset)
                .setPositiveButton(R.string.Delete, (dialog, which) -> deleteDataset(datasetMetadata))
                .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteDataset(final DatasetMetadata datasetMetadata) {
        // todo
        Toast.makeText(this, "Deleting... " + datasetMetadata.getDescription(), Toast.LENGTH_SHORT).show();
    }
}