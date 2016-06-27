package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.Image;

/**
 * @author Nearchos
 *         Created: 27-Apr-16
 */
public class ImageAdapter extends ArrayAdapter<Image>
{
    public static final String TAG = "ips";

    private final LayoutInflater layoutInflater;

    public ImageAdapter(final Context context, final Image [] images)
    {
        super(context, R.layout.list_item_image, images);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        final View view = convertView != null ? convertView : layoutInflater.inflate(R.layout.list_item_image, null);

        final Image image = getItem(position);

        // Creates a ViewHolder and store references to the two children views we want to bind data to.
        assert view != null;
        final ImageView bitmapImageView = (ImageView) view.findViewById(R.id.list_item_image_bitmap);
        final TextView imageUuidTextView = (TextView) view.findViewById(R.id.list_item_image_uuid);
        final TextView imageLabelTextView = (TextView) view.findViewById(R.id.list_item_image_label);

        // Bind the data efficiently with the holder.
        bitmapImageView.setImageBitmap(image.getImage());
        imageUuidTextView.setText(image.getUuid());
        imageLabelTextView.setText(image.getLabel());

        return view;
    }
}