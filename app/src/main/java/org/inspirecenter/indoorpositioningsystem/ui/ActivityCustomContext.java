package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.CustomContextElement;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.util.UUID;

public class ActivityCustomContext extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_context);

        this.listView = (ListView) findViewById(R.id.activity_custom_context_list_view);
    }

    private Location location;

    @Override
    protected void onResume() {
        super.onResume();

        location = (Location) getIntent().getSerializableExtra("location");
        if(location == null) {
            Toast.makeText(this, "Error: location not set in intent", Toast.LENGTH_SHORT).show();
            finish();
        }

        // populate list view
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
        final CustomContextElement [] customContextElements = DatabaseHelper.getCustomContextElements(sqLiteDatabase, location.getUuid());
        sqLiteDatabase.close();
        listView.setAdapter(new CustomContextAdapter(this, customContextElements));

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CustomContextElement customContextElement = customContextElements[i];
                final Intent intent = new Intent(ActivityCustomContext.this, ActivityAddCustomContext.class);
                intent.putExtra("customContext", customContextElement);
                startActivity(intent);
            }
        });
    }

    public void add(View view) {
        final CustomContextElement customContextElement = new CustomContextElement(UUID.randomUUID().toString(), location.getUuid(), "", "", true);
        final Intent intent = new Intent(ActivityCustomContext.this, ActivityAddCustomContext.class);
        intent.putExtra("customContext", customContextElement);
        startActivity(intent);
    }
}
