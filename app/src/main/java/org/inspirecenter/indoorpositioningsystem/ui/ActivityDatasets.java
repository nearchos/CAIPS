package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.inspirecenter.indoorpositioningsystem.R;

public class ActivityDatasets extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datasets);
    }

    public void add(final View view) {
        startActivity(new Intent(this, ActivityLocations.class));
    }
}