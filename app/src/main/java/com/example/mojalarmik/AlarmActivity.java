package com.example.mojalarmik;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {


    private AlarmManager alarmManager;
    private TimePicker alarmTimePicker;
    private CheckBox smsCheckbox;
    private ToggleButton toggleAlarm;
    private PendingIntent pendingIntent;

    private static final int SCAN_BARCODE_REQUEST = 1;
    private static final int PICK_CONTACT_REQUEST = 2;
    private static final int PICK_MUSIC_REQUEST = 3;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmTimePicker = findViewById(R.id.alarmTimePicker);
        smsCheckbox = findViewById(R.id.checkSMS);
        toggleAlarm = findViewById(R.id.alarmToggle);

        alarmTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                turnOffAlarm();
            }
        });

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Toolbar mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
    }


    private long getAlarmTime() {
        long timeInMillis;

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        startTime.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        startTime.set(Calendar.SECOND, 0);

        Calendar now = Calendar.getInstance();
        if (now.before(startTime)) {
            timeInMillis = startTime.getTimeInMillis();
        } else {
            startTime.add(Calendar.DATE, 1);
            timeInMillis = startTime.getTimeInMillis();
        }

        return timeInMillis;
    }

    private void turnOffAlarm() {
        Log.d("AlarmActivity", "Alarm Off");
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            toggleAlarm.setChecked(false);
        }
    }

    private void turnOnAlarm(){
        Log.d("AlarmActivity", "Alarm On");
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        myIntent.putExtra("SMS",smsCheckbox.isChecked());
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getAlarmTime(), pendingIntent);
    }

    public void onToggleClicked(View view) {
        if (toggleAlarm.isChecked()) {
            if (DataManager.readBarcode(this).isEmpty()) {
                toggleAlarm.setChecked(false);
                Toast.makeText(this, getString(R.string.not_choosen_barcode), Toast.LENGTH_LONG).show();
            } else {
               turnOnAlarm();
            }
        } else {
            turnOffAlarm();
        }
    }

    public void onCheckSMSClicked(View view) {
        if (smsCheckbox.isChecked() && !checkSMSPermissionAndContact()) {
            smsCheckbox.setChecked(false);
        }
    }

    private boolean checkSMSPermissionAndContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
            return false;
        }

        if (DataManager.readNumber(this).isEmpty()) {
            Toast.makeText(this, getString(R.string.choose_contact), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.disabled_sms), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void scanBarcode() {
        try {
            //check if can scan barcode otherwise install app
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, SCAN_BARCODE_REQUEST);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    private void pickMusic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*"); // specify "audio/mp3" to filter only mp3 files
        startActivityForResult(intent,PICK_MUSIC_REQUEST);
    }

    private void showInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
        builder.setTitle(getString(R.string.info_title));
        builder.setMessage(getString(R.string.info));
        builder.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_barcode) {
            scanBarcode();
            return true;
        } else if (id == R.id.action_add_contact) {
            pickContact();
        } else if (id == R.id.action_help) {
            showInfo();
        } else if(id == R.id.music_pick) {
            pickMusic();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_BARCODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                DataManager.writeBarcode(this, contents); // save new barcode
                Toast.makeText(this, getString(R.string.barcode_saved), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                DataManager.writeNumber(this, number);
                Toast.makeText(this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_MUSIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri audio = data.getData(); //declared above Uri audio;
                DataManager.writeURIMusic(this, audio.toString());
                Toast.makeText(this, getString(R.string.music_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
