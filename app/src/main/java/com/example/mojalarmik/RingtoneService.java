package com.example.mojalarmik;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RingtoneService extends Service {

    private MediaPlayer player;
    private Timer timer;

    private long startTime;
    private long lastingTime;
    private boolean smsSent = false;
    private boolean smsEnable;

    private static final long SMS_TIME = 100000;

    public RingtoneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("ring" + intent.getBooleanExtra("SMS",false));
        smsEnable = intent.getBooleanExtra("SMS",false);
        startTime = System.currentTimeMillis();

        try {

            player = MediaPlayer.create(this, getRingtone());
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                lastingTime = System.currentTimeMillis() - startTime;
                System.out.println(lastingTime);
                if (smsEnable && !smsSent && lastingTime > SMS_TIME) {
                    smsSent = true;
                    sendSMS();
                }
            }
        }, 0, 1000);
        return START_STICKY;
    }

    private Uri getRingtone() {
        String stringUri = DataManager.readURIMusic(this);
        if(stringUri.isEmpty()) {
           return Settings.System.DEFAULT_RINGTONE_URI;
        } else {
            return Uri.parse(stringUri);
        }
    }

    void sendSMS() {
        try {
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(DataManager.readNumber(this), null, getString(R.string.send_sms), null, null);
            Log.d("RingtoneService", "SMS Sent Successfully");
        } catch (Exception e) {
            Log.d("RingtoneService", "SMS Error");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        timer.cancel();
    }
}
