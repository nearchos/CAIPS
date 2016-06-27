package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.inspirecenter.indoorpositioningsystem.R;

/**
 * @author Nearchos
 *         Created: 27-Apr-16
 */
public class CrossHairView extends View  {

    private final Paint textPaint = new Paint();
    private final Paint linePaint = new Paint();

    public CrossHairView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CrossHairView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        textPaint.setColor(context.getResources().getColor(R.color.colorWhite));
        textPaint.setShadowLayer(1, 1, 1, context.getResources().getColor(R.color.colorPrimaryDark));
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(28f);

        linePaint.setColor(context.getResources().getColor(R.color.colorAccent));
        linePaint.setPathEffect(new DashPathEffect(new float[]{40, 40}, 0));
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        // draw the bitmap at offset
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        // draw the crosshair
        canvas.drawLine(canvasWidth/2, 0, canvasWidth/2, canvasHeight, linePaint); // vertical line
        canvas.drawLine(0, canvasHeight/2, canvasWidth, canvasHeight/2, linePaint); // horizontal line

        // draw coordinates and text (top left)
        canvas.drawText(lat + "," + lng, 32, 32, textPaint);
        canvas.drawText(floorName, 32, 64, textPaint);
        canvas.drawText(locationName, 32, 96, textPaint);
    }

    private String floorName = "unknown";
    private String locationName = "unknown";

    void setNames(final String floorName, final String locationName) {
        this.floorName = floorName;
        this.locationName = locationName;
        invalidate();
    }

    private double lat = 0d;
    private double lng = 0d;

    void setSelectedCoordinates(final double lat, final double lng) {
        this.lat = lat;
        this.lng = lng;
        invalidate();
    }
}