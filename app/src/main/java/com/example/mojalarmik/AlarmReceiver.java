package com.example.mojalarmik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Objects;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AlarmReceiver", "Starting service " + SystemClock.elapsedRealtime());

        Intent iService = new Intent(context, RingtoneService.class);
        iService.putExtra("SMS", Objects.requireNonNull(intent.getExtras()).getBoolean("SMS"));
        context.startService(iService);

        Intent iActivity = new Intent(context, WakingUpActivity.class);
        iActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(iActivity);

        setResultCode(Activity.RESULT_OK);
    }
}
