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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import org.greenrobot.eventbus.EventBus;

public class LocationService extends Service implements AMapLocationListener {


    private AMapLocationClientOption mLocationOption;

    private PendingIntent mAlarmPendingIntent = null;

    private AlarmManager mAlarmManager = null;

    private Vibrator mVibrator;

    private AMapLocationClient mLocationClient;

    private int mInterval;

    private float mRadius;

    private LocationEntity mLoactionEntity;

    private PendingIntent mPendingIntent;
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
                startVaibrate();
            }else if(status == 2) {
                stopVibrate();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initAlarm();

        registerAlarmReceiver();
        registerLocationReceiver();

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        mLoactionEntity = (LocationEntity) intent.getSerializableExtra("entity");

        if (mPendingIntent != null && mLocationClient != null) {
            mLocationClient.removeGeoFenceAlert(mPendingIntent);
        }else{
            initLocationClient();
        }

        if (mLoactionEntity != null) {
            mInterval = mLoactionEntity.getInterval();
            mRadius = mLoactionEntity.getRadius();
            mLocationClient.addGeoFenceAlert("fenceId", mLoactionEntity.getLatitude(), mLoactionEntity.getLongitude(), mRadius, -1, mPendingIntent);// 39.978578, 116.352245

            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setGpsFirst(false);
            mLocationOption.setInterval(mInterval);

            mLocationClient.setLocationOption(mLocationOption);

            mLocationClient.startLocation();
            EventBus.getDefault().post(true);
        }

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


    private void initLocationClient() {

        Intent intent = new Intent("com.location.buszzz.broadcast");
        mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationClient.setLocationListener(this);

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

    private void startVaibrate() {
        long[] pattern = {100, 200, 100, 200, 100, 300, 100, 400, 100, 500, 100, 600};
        mVibrator.vibrate(pattern, pattern.length / 2);
    }

    private void stopVibrate() {
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            EventBus.getDefault().post(aMapLocation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAlarmReceiver != null) {
            unregisterReceiver(mAlarmReceiver);
        }

        if (mLocationReceiver != null) {
            unregisterReceiver(mLocationReceiver);
        }

        if (mVibrator != null) {
            mVibrator.cancel();
        }

        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }

        if (mLoactionEntity != null) {
            LocationDao dao = new LocationDao(this);
            mLoactionEntity.setSelected(0);
            dao.update(mLoactionEntity);
        }



    }

}
