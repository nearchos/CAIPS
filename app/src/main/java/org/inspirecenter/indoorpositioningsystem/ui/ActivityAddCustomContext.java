package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.CustomContextElement;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

public class ActivityAddCustomContext extends AppCompatActivity {

    private EditText uuidEditText;
    private EditText locationUuidEditText;
    private EditText nameEditText;
    private EditText valueEditText;
    private EditText checkedEditText;
    private CheckBox checkedCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_context);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        uuidEditText = (EditText) findViewById(R.id.custom_context_uuid);
        locationUuidEditText = (EditText) findViewById(R.id.custom_context_location_uuid);
        nameEditText = (EditText) findViewById(R.id.custom_context_name);
        valueEditText = (EditText) findViewById(R.id.custom_context_value);
        checkedEditText = (EditText) findViewById(R.id.custom_context_checked_text);
        checkedCheckBox = (CheckBox) findViewById(R.id.custom_context_checked);

        checkedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                checkedEditText.setText(isChecked ? getString(R.string.Active) : getString(R.string.Inactive));
            }
        });
    }

    private CustomContextElement customContextElement;

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        customContextElement = (CustomContextElement) intent.getSerializableExtra("customContext");
        if(customContextElement == null) {
            Toast.makeText(this, "Error: custom context not found in intent", Toast.LENGTH_SHORT).show();
            finish();
        }

        uuidEditText.setText(customContextElement.getUuid());
        locationUuidEditText.setText(customContextElement.getLocationUuid());
        nameEditText.setText(customContextElement.getName());
        valueEditText.setText(customContextElement.getValue());
        checkedCheckBox.setChecked(customContextElement.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_or_edit_custom_context, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_delete_custom_context:
                final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
                final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
                DatabaseHelper.deleteCustomContext(sqLiteDatabase, customContextElement);
                sqLiteDatabase.close();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void save(View view) {
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();

        DatabaseHelper.addCustomContext(
                sqLiteDatabase,
                customContextElement.getUuid(),
                customContextElement.getLocationUuid(),
                nameEditText.getText().toString().trim(),
                valueEditText.getText().toString().trim(),
                checkedCheckBox.isChecked());
        sqLiteDatabase.close();
        finish();
    }
}