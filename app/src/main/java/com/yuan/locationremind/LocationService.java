package com.yuan.locationremind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yuan.locationremind.entity.LocationEntity;

import org.greenrobot.eventbus.EventBus;

public class LocationService extends Service implements AMapLocationListener {


    private AMapLocationClientOption mLocationOption;

    private PendingIntent mAlarmPendingIntent = null;

    private AlarmManager mAlarmManager = null;

    private Vibrator mVibrator;

    private AMapLocationClient mLocationClient;
    /**
     * 闹钟广播
     */
    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("om.location.buszzz.alarm")) {
                if (null != mLocationClient) {
                    mLocationClient.startLocation();
                }
            }
        }
    };
    /**
     * 围栏区域变化广播
     */
    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int status = bundle.getInt("event");
            if (status == 1) {
//                vibrate();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initAlarm();

        registerAlarmReceiver();
        registerLocationReceiver();

        initLocationOption();
        initLocationClient();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initAlarm() {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("com.location.buszzz.alarm");
        mAlarmPendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int alarmInterval = 10;
        if (null != mAlarmManager) {
            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmInterval * 1000, mAlarmPendingIntent);
        }
    }

    private void initLocationOption() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setGpsFirst(false);
        mLocationOption.setInterval(2000);
    }

    private void initLocationClient() {

        String latitude = (String) SharedPreferenceHelper.getData(this, "latitude", "0");
        String longitude = (String) SharedPreferenceHelper.getData(this, "longitude", "0");

//        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
//            Toast.makeText(this, "请设置经纬度！", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Intent intent = new Intent("com.location.buszzz.broadcast");
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationClient.addGeoFenceAlert("fenceId", Double.valueOf(latitude), Double.valueOf(longitude), 100, -1, mPendingIntent);// 39.978578, 116.352245
        mLocationClient.setLocationListener(this);
        initLocationOption();
        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();
        EventBus.getDefault().post(true);
    }

    private void registerAlarmReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.location.buszzz.alarm");
        registerReceiver(mAlarmReceiver, filter);
    }

    private void registerLocationReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("com.location.buszzz.broadcast");
        registerReceiver(mLocationReceiver, filter);
    }

    private void vibrate() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 200, 100, 200, 100, 300, 100, 400, 100, 500, 100, 600};
        mVibrator.vibrate(pattern, pattern.length / 2);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {

            Log.d("LocationService", aMapLocation.getAddress());

            LocationEntity entity = new LocationEntity();
            entity.setAddress(aMapLocation.getAddress());
            entity.setLatitude(aMapLocation.getLatitude());
            entity.setLongitude(aMapLocation.getLongitude());
            EventBus.getDefault().post(entity);
        }
    }
}
