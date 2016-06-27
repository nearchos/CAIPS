package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Nearchos Paspallis
 * Created on 16/06/2014.
 */
public class IndoorsLocationView extends View
{
    public static final String TAG = "ips";

    private final Paint linePaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint trainingPaint = new Paint();
    public static final float tR = 10f; // training point radius
    private Bitmap mapBitmap = null;
    private Location location;
    private Floor floor;

    public IndoorsLocationView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public IndoorsLocationView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        textPaint.setColor(context.getResources().getColor(R.color.colorAccent));
        textPaint.setTextSize(20f);

        linePaint.setColor(context.getResources().getColor(R.color.colorAccent));
        linePaint.setPathEffect(new DashPathEffect(new float[]{40, 40}, 0));
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        trainingPaint.setColor(context.getResources().getColor(R.color.colorAccent));
        trainingPaint.setStrokeWidth(2);
        trainingPaint.setTextSize(20f);
        trainingPaint.setFakeBoldText(true);
    }

    int bitmapWidth = 0;
    int bitmapHeight = 0;

    private double lat = 0d;
    private double lng = 0d;

    private Map<Training,Double> trainingsToScores = null;

    /**
     *
     * @param trainingsToScores set to null to stop painting training points
     */
    void setTrainingPoints(final Map<Training,Double> trainingsToScores)
    {
        this.trainingsToScores = trainingsToScores;
    }

    private boolean showTrainingPoints = true;

    void setShowTrainingPoints(final boolean state)
    {
        this.showTrainingPoints = state;
        invalidate();
    }

    void init(final Location location, final Floor floor)
    {
        this.location = location;
        this.floor = floor;
        // todo revise
        new LoadImageAsyncTask().execute(); // load image asynchronously

        if(lat == 0 && lng == 0)
        {
            // init to center of image
            lat = (floor.getTopLeftLat() + floor.getBottomRightLat()) / 2;
            lng = (floor.getTopLeftLng() + floor.getBottomRightLng()) / 2;
        }
    }

    private double [] getSelectedCoordinates()
    {
        return new double [] { lat, lng };
    }

    void setSelectedCoordinates(final double lat, final double lng)
    {
        this.lat = lat;
        this.lng = lng;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        if (mapBitmap != null)
        {
            // draw the bitmap at offset
            final double xx = (lat - floor.getTopLeftLat()) / (floor.getBottomRightLat() - floor.getTopLeftLat());
            final double yy = (lng - floor.getTopLeftLng()) / (floor.getBottomRightLng() - floor.getTopLeftLng());

            float bitmapOffsetX = canvas.getWidth()/2 - (float) xx * mapBitmap.getWidth();
            float bitmapOffsetY = canvas.getHeight()/2 - (float) yy * mapBitmap.getHeight();

            canvas.drawBitmap(mapBitmap, bitmapOffsetX, bitmapOffsetY, null);

            // draw the training points
            if(showTrainingPoints)
            {
                for(final Map.Entry<Training,Double> trainingToScore : trainingsToScores.entrySet())
                {
                    // compute position and draw the training points (e.g. circles)
                    double tDX = (trainingToScore.getKey().getLat() - floor.getTopLeftLat()) / (floor.getBottomRightLat() - floor.getTopLeftLat());
                    double tDY = (trainingToScore.getKey().getLng() - floor.getTopLeftLng()) / (floor.getBottomRightLng() - floor.getTopLeftLng());
                    float tX = bitmapOffsetX + (float) tDX * mapBitmap.getWidth();
                    float tY = bitmapOffsetY + (float) tDY * mapBitmap.getHeight();
                    canvas.drawCircle(tX, tY, tR, trainingPaint);
                    final String scoreS = MessageFormat.format("{0,number,#.##%}", trainingToScore.getValue());
                    canvas.drawText(scoreS, tX - 30, tY + 30, trainingPaint);
                }
            }
        }

        // draw the cross-hair
        canvas.drawLine(canvas.getWidth()/2, 0, canvas.getWidth()/2, canvas.getHeight(), linePaint); // vertical line
        canvas.drawLine(0, canvas.getHeight()/2, canvas.getWidth(), canvas.getHeight()/2, linePaint); // horizontal line

        // draw coordinates text (top left)
        final double [] coordinates = getSelectedCoordinates();
        canvas.drawText(coordinates[0] + "," + coordinates[1], 32, 32, textPaint);

        // draw menu_location and floor text (bottom left)
        if(floor != null) canvas.drawText(floor.getName(), 32, canvas.getHeight()-54, textPaint);
        if(location != null) canvas.drawText(location.getName(), 32, canvas.getHeight()-22, textPaint);

    }

    private class LoadImageAsyncTask extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] params)
        {
            try
            {
                final URL url = new URL(floor.getImageUrl());
                final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                final InputStream inputStream = httpURLConnection.getInputStream();
                mapBitmap = BitmapFactory.decodeStream(inputStream);
                bitmapWidth = mapBitmap.getWidth();
                bitmapHeight = mapBitmap.getHeight();
            }
            catch (IOException ioe)
            {
                Log.e(TAG, ioe.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            invalidate(); // update UI
        }
    }
}