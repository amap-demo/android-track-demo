package com.amap.locmonitor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.amap.locmonitorsdk.AMapTrackListener;
import com.amap.locmonitorsdk.AMapTrackManager;
import com.amap.locmonitorsdk.AMapTrackOption;

/**
 * Created by liangchao_suxun on 2017/7/10.
 */

public class MainActivity extends Activity {
    private AMapTrackManager mTrackManager = null;

    private EditText mPhoneET;
    private EditText mPlateET;

    private AMapTrackListener listener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneET = (EditText)findViewById(R.id.phoneet);
        mPlateET = (EditText)findViewById(R.id.plateet);

        if (mTrackManager == null) {
            listener = new AMapTrackListener() {
                @Override
                public void onTrackStart(int errCode, String msg) {
                    Log.w("haha", "on track start " + errCode + " , " + msg);
                }

                @Override
                public void onFail(int errCode, String msg) {
                    Log.w("haha", "onFail " + errCode + " , " + msg);
                }

                @Override
                public void onTrackFinish(int errCode, String msg) {
                    Log.w("haha", " onTrackFinish " + errCode + " , " + msg);
                }
            };
        }
    }

    @TargetApi(VERSION_CODES.KITKAT_WATCH)
    public void onStartTrack(View view) {
        Toast.makeText(this, "start monitor ", Toast.LENGTH_SHORT).show();

        AMapTrackOption option = new AMapTrackOption(mPhoneET.getText().toString(), mPlateET.getText().toString());

        mTrackManager = AMapTrackManager.newInstance(option);
        mTrackManager.addTrackListener(listener);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.this.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Builder builder = new Builder(MainActivity.this)
            .setContentTitle("TITLE")
            .setContentText("DESC")
            .setContentIntent(contentIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())
            .setOngoing(true);

        mTrackManager.startTrack(this, builder.getNotification());
    }

    public void onStopTrack(View view) {
        Toast.makeText(this, "stop monitor ", Toast.LENGTH_SHORT).show();
        if (mTrackManager == null) {
            return;
        }
        mTrackManager.stopTrack(this);
    }

}
