package org.nemux.bakky.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.nemux.bakky.service.PushAndPull;

/**
 * Created by nemux on 28/07/15.
 */
public class BootReceiver extends BroadcastReceiver {

    final static String TAG = "BakkyBootdReceiver";

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.w(TAG, "ok i got BootCompleted ... starting bakky service ...");
        context.startService(new Intent(context, PushAndPull.class));
    }
}
