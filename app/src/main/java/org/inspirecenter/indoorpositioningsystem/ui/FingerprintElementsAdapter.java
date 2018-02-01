package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import org.inspirecenter.indoorpositioningsystem.R;
import org.inspirecenter.indoorpositioningsystem.data.FingerprintElement;


public class FingerprintElementsAdapter extends ArrayAdapter {

    public FingerprintElementsAdapter(@NonNull Context context, FingerprintElement[] fingerprintElements) {
        super(context, R.layout.fingerprint_element_item);
    }
}
