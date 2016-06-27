package org.inspirecenter.indoorpositioningsystem.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * @author Nearchos
 *         Created: 27-Apr-16
 */
public class Image {
    private final String uuid;
    private final String label;
    private final byte [] data;

    public Image(final String uuid, final String label, final byte [] data) {
        this.uuid = uuid;
        this.label = label;
        this.data = data;
    }

    public Image(final String uuid, final String label, final Bitmap bitmap) {
        this.uuid = uuid;
        this.label = label;
        this.data = getBitmapAsByteArray(bitmap);
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }

    public byte [] getData() {
        return data;
    }

    public Bitmap getImage() {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}