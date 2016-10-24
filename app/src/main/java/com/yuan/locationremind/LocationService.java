package com.yuan.locationremind;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.db.LocationDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LocationService extends Service implements AMapLocationListener {


    private Vibrator mVibrator;
    private AMapLocationClient mLocationClient;
    private LocationEntity mLocationEntity;
    private PendingIntent mPendingIntent;
    private AMapLocation mLatestLocation;
    private AlertDialog mRemindDialog;

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
                showTip();
                startVibrate();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initAlarm();

        registerAlarmReceiver();
        registerLocationReceiver();
        EventBus.getDefault().register(this);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        mLocationEntity = (LocationEntity) intent.getSerializableExtra("entity");

        if (mPendingIntent != null && mLocationClient != null) {
            mLocationClient.removeGeoFenceAlert(mPendingIntent);
        } else {
            initLocationClient();
        }

        if (mLocationEntity != null) {
            int interval = mLocationEntity.getInterval() * 1000;
            float radius = mLocationEntity.getRadius();
            mLocationClient.addGeoFenceAlert("fenceId", mLocationEntity.getLatitude(), mLocationEntity.getLongitude(), radius, -1, mPendingIntent);// 39.978578, 116.352245

            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationOption.setNeedAddress(true);
            locationOption.setGpsFirst(false);
            locationOption.setInterval(interval);
            mLocationClient.setLocationOption(locationOption);

            mLocationClient.startLocation();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initAlarm() {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("com.location.buszzz.alarm");
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int alarmInterval = 10;
        if (null != alarmManager) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmInterval * 1000, alarmPendingIntent);
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

    /**
     * 开启震动
     */
    private void startVibrate() {
        long[] pattern = {100, 200, 100, 200, 100, 300, 100, 400, 100, 500, 100, 600};
        mVibrator.vibrate(pattern, pattern.length / 2);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            mLatestLocation = aMapLocation;
            EventBus.getDefault().post(aMapLocation);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void restoreLatestLocation(LocationListActivity entity) {
        if (mLatestLocation != null) {
            EventBus.getDefault().post(mLatestLocation);
        }
    }



    private void release() {

        try {
            if (mAlarmReceiver != null) {
                unregisterReceiver(mAlarmReceiver);
                mAlarmReceiver = null;
            }

            if (mLocationReceiver != null) {
                unregisterReceiver(mLocationReceiver);
                mLocationReceiver = null;
            }

            if (mVibrator != null) {
                mVibrator.cancel();
            }

            if (mLocationClient != null) {
                mLocationClient.onDestroy();
            }

            if (mLocationEntity != null) {
                LocationDao dao = new LocationDao(this);
                mLocationEntity.setSelected(0);
                dao.update(mLocationEntity);
            }

            dismissTip();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showTip() {

        Context applicationContext = getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(applicationContext, android.R.style.Theme_DeviceDefault_Light_Dialog));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                release();
            }
        });
        builder.setTitle("提示");
        String add = "";
        if (mLocationEntity != null) {
            add = mLocationEntity.getAddress();
        }
        builder.setMessage("到达“" + add + "”附近，请准备下车！");
        mRemindDialog = builder.create();
        mRemindDialog.setCanceledOnTouchOutside(false);
        if(mRemindDialog.getWindow() != null) {
            mRemindDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mRemindDialog.show();


    }

    public void dismissTip() {
        if (mRemindDialog != null && mRemindDialog.isShowing()) {
            EventBus.getDefault().post(this);
            mRemindDialog.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
        EventBus.getDefault().unregister(this);

    }

}
