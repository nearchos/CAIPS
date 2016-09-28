package org.inspirecenter.indoorpositioningsystem.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Image;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivitySelectImage extends AppCompatActivity {
    public static final String TAG = "ips";

    private ListView listView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.activity_select_image_list_view);
    }

    public static final String INTENT_EXTRA_SELECTED_IMAGE_UUID_KEY = "image_uuid";

    @Override
    protected void onResume() {
        super.onResume();

        updateListView();
    }

    private void updateListView() {
        // get images
        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        final Image [] images = DatabaseHelper.getAllImages(databaseOpenHelper.getReadableDatabase());

        imageAdapter = new ImageAdapter(this, images);
        listView.setAdapter(imageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent data = new Intent();
                data.putExtra(INTENT_EXTRA_SELECTED_IMAGE_UUID_KEY, images[position].getUuid());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static final int REQUEST_CODE_PICK_IMAGE = 42;

    public void add(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(ActivitySelectImage.this, "Error: empty image selected", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                final InputStream inputStream = getContentResolver().openInputStream(data.getData());
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                final int width = bitmap.getWidth();
                final int height = bitmap.getHeight();
                final double widthToHeightRatio = width * 1d / height;
                final Bitmap projectedBitmap;
                if(width >= height && width > 1024) {
                    projectedBitmap = Bitmap.createScaledBitmap(bitmap, 1024, (int) (1024 / widthToHeightRatio), false);
                } else if(height > width && height > 1024) {
                    projectedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (1024 * widthToHeightRatio), 1024, false);
                } else {
                    projectedBitmap = bitmap;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select image label");
                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String uuid = UUID.randomUUID().toString();
                        final String label = input.getText().toString();
                        final Image image = new Image(uuid, label, projectedBitmap);
                        final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(ActivitySelectImage.this);
                        DatabaseHelper.addImage(databaseOpenHelper.getWritableDatabase(), image);
                        updateListView();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            } catch (FileNotFoundException fnfe) {
                Log.e(TAG, fnfe.getMessage());
            }
        }
    }
}