package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.Installation;
import org.inspirecenter.indoorpositioningsystem.R;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Installation.id(this);

        startActivity(new Intent(this, ActivityAuthenticate.class));
        finish();
    }
}