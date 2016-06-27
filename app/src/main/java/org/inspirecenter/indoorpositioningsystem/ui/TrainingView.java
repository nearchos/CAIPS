package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Image;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseHelper;
import org.inspirecenter.indoorpositioningsystem.db.DatabaseOpenHelper;


/**
 * @author Nearchos Paspallis
 * Created on 16/06/2014.
 */
public class TrainingView extends View
{
    public static final String TAG = "ips";

    private final Paint linePaint = new Paint();
    private final Paint textPaint = new Paint();
    private Bitmap mapBitmap = null;
    private Location location;
    private Floor floor;

    public TrainingView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TrainingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        textPaint.setColor(context.getResources().getColor(R.color.colorAccent));
        textPaint.setTextSize(20f);

        linePaint.setColor(context.getResources().getColor(R.color.colorAccent));
        linePaint.setPathEffect(new DashPathEffect(new float[]{40, 40}, 0));
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    int bitmapWidth = 0;
    int bitmapHeight = 0;

    void init(final Location location, final Floor floor)
    {
        this.location = location;
        this.floor = floor;
        new LoadImageAsyncTask().execute(getContext()); // load image asynchronously
    }

    double [] getSelectedCoordinates()
    {
        // offset x - from: -(bitmapWidth - canvasWidth/2), to: canvasWidth/2
        // offset y - from: -(bitmapHeight - canvasHeight/2), to: canvasHeight/2

        double px = 1d - (selectedOffsetX + bitmapWidth - canvasWidth/2) / bitmapWidth;
        double py = 1d - (selectedOffsetY + bitmapHeight - canvasHeight/2) / bitmapHeight;

        double lat = floor.getTopLeftLat() + (floor.getBottomRightLat() - floor.getTopLeftLat()) * px;
        double lng = floor.getTopLeftLng() + (floor.getBottomRightLng() - floor.getTopLeftLng()) * py;

        return new double[] {lat, lng};
    }

    private float startX = 0f;
    private float startY = 0f;
    private float selectedOffsetX = 0f;
    private float selectedOffsetY = 0f;
    private float dX = 0f;
    private float dY = 0f;

    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent)
    {
        final int action = motionEvent.getActionMasked();
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
//                Log.d("deb","Action was DOWN");
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                dX = motionEvent.getX()-startX;
                dY = motionEvent.getY()-startY;
                invalidate();
//                Log.d("deb","Action was MOVE - " + dX + "," + dY);
                return true;
            case (MotionEvent.ACTION_UP) :
//                Log.d("deb","Action was UP");
                selectedOffsetX += dX;
                selectedOffsetY += dY;
                dX = 0;
                dY = 0;
                return true;
            case (MotionEvent.ACTION_CANCEL) :
//                Log.d("deb","Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
//                Log.d("deb","Movement occurred outside bounds of current screen element");
                return true;
            default :
                return super.onTouchEvent(motionEvent);
        }
    }

    private int canvasWidth;
    private int canvasHeight;

    @Override
    public void onDraw(Canvas canvas)
    {
        // draw the bitmap at offset
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        if (mapBitmap != null)
        {
            if(selectedOffsetX > canvasWidth/2) selectedOffsetX = canvasWidth/2;
            if(selectedOffsetY > canvasHeight/2) selectedOffsetY = canvasHeight/2;
            if(selectedOffsetX < -(bitmapWidth - canvasWidth/2)) selectedOffsetX = -(bitmapWidth - canvasWidth/2);
            if(selectedOffsetY < -(bitmapHeight - canvasHeight/2)) selectedOffsetY = -(bitmapHeight - canvasHeight/2);

            float tempX = selectedOffsetX + dX;
            if(tempX > canvasWidth/2) tempX = canvasWidth/2;
            if(tempX < -(bitmapWidth - canvasWidth/2)) tempX = -(bitmapWidth - canvasWidth/2);

            float tempY = selectedOffsetY + dY;
            if(tempY > canvasHeight/2) tempY = canvasHeight/2;
            if(tempY < -(bitmapHeight - canvasHeight/2)) tempY = -(bitmapHeight - canvasHeight/2);

            canvas.drawBitmap(mapBitmap, tempX, tempY, null);
        }

        // draw the crosshair
        canvas.drawLine(canvasWidth/2, 0, canvasWidth/2, canvasHeight, linePaint); // vertical line
        canvas.drawLine(0, canvasHeight/2, canvasWidth, canvasHeight/2, linePaint); // horizontal line

        // draw coordinates text (top left)
        final double [] coordinates = getSelectedCoordinates();
        canvas.drawText(coordinates[0] + "," + coordinates[1], 32, 32, textPaint);

        // draw menu_location and floor text (bottom left)
        canvas.drawText(floor.getName(), 32, canvasHeight-54, textPaint);
        canvas.drawText(location.getName(), 32, canvasHeight-22, textPaint);
    }

    private class LoadImageAsyncTask extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] params)
        {
            final Context context = (Context) params[0];
//            try
//            {
//                final URL url = new URL(floor.getImageUrl());
//                final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//                final InputStream inputStream = httpURLConnection.getInputStream();
//                mapBitmap = BitmapFactory.decodeStream(inputStream);
                final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(context);
                final Image image = DatabaseHelper.getImage(databaseOpenHelper.getReadableDatabase(), floor.getImageUrl());
                mapBitmap = image.getImage();
                bitmapWidth = mapBitmap.getWidth();
                bitmapHeight = mapBitmap.getHeight();
//            }
//            catch (IOException ioe)
//            {
//                Log.e(TAG, ioe.getMessage());
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            invalidate(); // update UI
        }
    }
}