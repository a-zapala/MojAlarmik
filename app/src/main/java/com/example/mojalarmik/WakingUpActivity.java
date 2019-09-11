package com.example.mojalarmik;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class WakingUpActivity extends AppCompatActivity {

    private static final int SCAN_BARCODE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waking_up);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("WakingUpActivity","destroy");
    }

    public void turnOffAlarm(View view) {
        // app have to be installed
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        startActivityForResult(intent, SCAN_BARCODE_REQUEST);
        return;
    }


    //can't press back
    @Override
    public void onBackPressed(){}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_BARCODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("SCAN_RESULT");
                String barcode = DataManager.readBarcode(this);

                if(result.equals(barcode)) {
                    stopService(new Intent(getBaseContext(), RingtoneService.class));
                    System.out.println("Kill");
                    finish();
                    return;
                } else {
                    Toast.makeText(this, getString(R.string.wrong_barcode), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
