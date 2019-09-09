package com.example.mojalarmik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // This is the Intent to deliver to our service.
        Intent n = new Intent(context, WakingUpActivity.class);
        // Start the service, keeping the device awake while it is launching.
        Log.d("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(n);
    }
}
