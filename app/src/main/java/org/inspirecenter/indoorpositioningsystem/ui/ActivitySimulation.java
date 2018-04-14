package org.inspirecenter.indoorpositioningsystem.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;
import org.inspirecenter.indoorpositioningsystem.model.DatasetMetadata;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.simulation.Algorithm;
import org.inspirecenter.indoorpositioningsystem.simulation.LocationEstimationAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class ActivitySimulation extends AppCompatActivity {

    public static final String TAG = "caips";

    public static final String EXTRA_KEY_DATASET_METADATA = "dataset_metadata";

    private View mainView;
    private ProgressBar progressBar;

    private TextView descriptionTextView;
    private TextView measurementEntriesTextView;
    private SeekBar splitSeekBar;
    private Spinner algorithmsSpinner;
    private TextView simulationConfigurationTextView;
    private ProgressBar simulationProgressBarHorizontal;

    private Button simulationButtonStart;
    private Button simulationButtonPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        this.mainView = findViewById(R.id.simulation_main);
        this.progressBar = findViewById(R.id.simulation_progress_bar);

        this.descriptionTextView = findViewById(R.id.simulation_description);
        this.measurementEntriesTextView = findViewById(R.id.simulation_measurement_entries);
        this.splitSeekBar = findViewById(R.id.simulation_seek_bar);
        this.algorithmsSpinner = findViewById(R.id.simulation_algorithm_spinner);
        this.simulationConfigurationTextView = findViewById(R.id.simulation_configuration);
        this.simulationProgressBarHorizontal = findViewById(R.id.simulation_progress_bar_horizontal);

        this.splitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { updateConfiguration(); }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { /* nothing */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { updateConfiguration(); }
        });

        this.algorithmsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Algorithm.values()));
        this.algorithmsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateConfiguration();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* nothing */ }
        });

        this.simulationButtonStart = findViewById(R.id.simulation_button_start);
        this.simulationButtonPause = findViewById(R.id.simulation_button_pause);
    }

    private DatasetMetadata datasetMetadata = null;
    private Dataset dataset = null;

    @Override
    protected void onResume() {
        super.onResume();

        this.datasetMetadata = (DatasetMetadata) getIntent().getSerializableExtra(EXTRA_KEY_DATASET_METADATA);
        if(datasetMetadata != null) {
            descriptionTextView.setText(datasetMetadata.getDescription());
            final int numOfMeasurementEntries = 0;
            descriptionTextView.setText(String.format(Locale.US, "%d", numOfMeasurementEntries));
        }

        simulationProgressBarHorizontal.setVisibility(View.GONE);
        refresh(null);
    }

    private void updateConfiguration() {
        descriptionTextView.setText(datasetMetadata.getDescription());
        final int numOfMeasurements = dataset.getNumberOfMeasurements();
        measurementEntriesTextView.setText(String.format(Locale.US, "%d", numOfMeasurements));
        final int split = splitSeekBar.getProgress();
        final String trainingSplit = String.format(Locale.US, "%1d%%", (split+1) * 10);
        final String testingSplit = String.format(Locale.US, "%1d%%", (9-split) * 10);
        final String algorithm = Algorithm.values()[algorithmsSpinner.getSelectedItemPosition()].toString();
        simulationConfigurationTextView.setText(getString(R.string.Simulation_configuration_with_params, numOfMeasurements, trainingSplit, testingSplit, algorithm));
    }

    private SimulateAsyncTask simulateAsyncTask = null;

    public void startSimulation(View view) {
        simulateAsyncTask = new SimulateAsyncTask();
        simulateAsyncTask.execute();
    }

    public void pauseSimulation(View view) {
        paused = !paused;
        if(paused) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void resetSimulation(View view) {
        // todo
        if(simulateAsyncTask != null) {
            simulateAsyncTask.cancel(true);
        }
    }

    public void refresh(View view) {
        new GetDatasetAsyncTask().execute();
    }

    class GetDatasetAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... aVoid) {
            try {
                return doHttpGet("https://caips-server.appspot.com/api/json/dataset?datasetId=" + datasetMetadata.getId());
            } catch (IOException ioe) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            final Gson gson = new Gson();
            Log.d(TAG, json);
            dataset = gson.fromJson(json, Dataset.class);

            progressBar.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);

            updateConfiguration();
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

    private boolean paused = false;

    class SimulateAsyncTask extends AsyncTask<Void, Integer, Void> {

        int i;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            simulationProgressBarHorizontal.setProgress(1);
            simulationProgressBarHorizontal.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
            simulationButtonStart.setEnabled(false);
            simulationButtonPause.setEnabled(true);
            paused = false;
            i = 0;

            prepareAlgorithm();
            prepareMeasurementEntrySplit();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            simulationProgressBarHorizontal.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final int numOfTestMeasurementEntries = testMeasurementEntries.size();
            while(i < numOfTestMeasurementEntries && !isCancelled()) {
//                try { Thread.sleep(50L); } catch (InterruptedException ie) {} // todo delete
                if(!paused) {
                    // handle 10 test measurements each time
                    final int j = Math.min(i+10, numOfTestMeasurementEntries);
                    final List<MeasurementEntry> nextMeasurementEntries = testMeasurementEntries.subList(i, j);
                    for(final MeasurementEntry nextMeasurementEntry : nextMeasurementEntries) {
                        processMeasurement(nextMeasurementEntry);
                    }
                    i = j;
                }
                publishProgress(i * 100 / numOfTestMeasurementEntries);
            }
            Log.d(TAG, "outcomes: " + outcomes);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            simulationProgressBarHorizontal.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
            simulationButtonStart.setEnabled(true);
            simulationButtonPause.setEnabled(false);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            simulationProgressBarHorizontal.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
            simulationButtonStart.setEnabled(true);
            simulationButtonPause.setEnabled(false);
            Toast.makeText(ActivitySimulation.this, R.string.Cancelled, Toast.LENGTH_SHORT).show();
        }
    }

    private LocationEstimationAlgorithm locationEstimationAlgorithm;

    private void prepareAlgorithm() {
        final int selectedAlgorithmPosition = algorithmsSpinner.getSelectedItemPosition();
        final Algorithm selectedAlgorithm = Algorithm.values()[selectedAlgorithmPosition];
        final Class<? extends LocationEstimationAlgorithm> locationEstimationAlgorithmClass = selectedAlgorithm.getLocationEstimationAlgorithm();
        try {
            locationEstimationAlgorithm = locationEstimationAlgorithmClass.newInstance();
            Log.d(TAG, "locationEstimationAlgorithm: " + locationEstimationAlgorithm);
        } catch (InstantiationException | IllegalAccessException e) {
            Toast.makeText(this, "Error while instantiating simulation algorithm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error while instantiating simulation algorithm", e);
        }
    }

    private Vector<MeasurementEntry> trainingMeasurementEntries = new Vector<>();
    private Vector<MeasurementEntry> testMeasurementEntries = new Vector<>();
    private Vector<Double> outcomes = new Vector<>();

    private void prepareMeasurementEntrySplit() {
        trainingMeasurementEntries.clear();
        testMeasurementEntries.clear();
        outcomes.clear();

        final List<MeasurementEntry> measurementEntries = dataset.getMeasurementEntries();

        final int split = splitSeekBar.getProgress(); // 0 means 10/90, 1 means 20/80... 8 means 90/10 (training/test)
        int numOfTrainings = (int) (dataset.getNumberOfMeasurements() * (split + 1) / 10d);
        int numOfTests = dataset.getNumberOfMeasurements() - numOfTrainings;

        Log.d(TAG, "Split to " + numOfTrainings + " trainings / " + numOfTests + " tests");
        Toast.makeText(this, "Split to " + numOfTrainings + " trainings / " + numOfTests + " tests", Toast.LENGTH_SHORT).show();

        Collections.shuffle(measurementEntries);

        if(numOfTrainings > 0) {
            trainingMeasurementEntries.addAll(measurementEntries.subList(0, numOfTrainings));
        }
        if(numOfTests > 0) {
            testMeasurementEntries.addAll(measurementEntries.subList(numOfTrainings, measurementEntries.size()));
        }
        Log.d(TAG, "Actual Split to " + trainingMeasurementEntries.size() + " trainings / " + testMeasurementEntries.size() + " tests");
    }

    private void processMeasurement(final MeasurementEntry measurementEntry) {
        final Coordinates coordinates = locationEstimationAlgorithm.estimateLocation(trainingMeasurementEntries, measurementEntry);
        final Coordinates actualCoordinates = measurementEntry.getCoordinates();

        outcomes.add(coordinates.distanceTo(actualCoordinates));
    }
}