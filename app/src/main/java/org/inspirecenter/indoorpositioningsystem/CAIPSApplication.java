package org.inspirecenter.indoorpositioningsystem;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * @author Nearchos
 *         Created: 29-Nov-16
 */

public class CAIPSApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
