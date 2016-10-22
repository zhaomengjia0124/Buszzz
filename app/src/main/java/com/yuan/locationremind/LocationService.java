package com.yuan.locationremind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LocationService extends Service implements AMapLocationListener {


    TextView textView;
    private Vibrator mVibrator;
    private AMapLocationClient mLocationClient;
    private LocationEntity mLocationEntity;
    private PendingIntent mPendingIntent;
    private AMapLocation mLatestLocation;
    private WindowManager mWindowMnanager;
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
            } else if (status == 2) {
                dismissTip();
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
        EventBus.getDefault().register(this);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        mLocationEntity = (LocationEntity) intent.getSerializableExtra("entity");

        if (mPendingIntent != null && mLocationClient != null) {
            mLocationClient.removeGeoFenceAlert(mPendingIntent);
        } else {
            initLocationClient();
        }

        if (mLocationEntity != null) {
            int interval = mLocationEntity.getInterval();
            float radius = mLocationEntity.getRadius();
            mLocationClient.addGeoFenceAlert("fenceId", mLocationEntity.getLatitude(), mLocationEntity.getLongitude(), radius, -1, mPendingIntent);// 39.978578, 116.352245

            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationOption.setNeedAddress(true);
            locationOption.setGpsFirst(false);
            locationOption.setInterval(interval);
            mLocationClient.setLocationOption(locationOption);

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

    private void startVibrate() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        release();

    }

    private void release() {

        try {
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

        mWindowMnanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
        params.format = PixelFormat.TRANSLUCENT;// 支持透明
        //params.format = PixelFormat.RGBA_8888;
//        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
        params.width = 600;//窗口的宽和高
        params.height = 200;
        params.gravity = Gravity.CENTER;
        params.x = 0;//窗口位置的偏移量
        params.y = 0;

        textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.parseColor("#ffbf40"));
        textView.setPadding(50, 50, 50, 50);
        textView.setText("到站了，点我关闭提醒！");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });

        if (textView.getParent() == null) {
            mWindowMnanager.addView(textView, params);
        }

    }

    public void dismissTip() {
        if (mWindowMnanager != null && textView != null && textView.getParent() != null) {
            mWindowMnanager.removeView(textView);
            EventBus.getDefault().post(this);
        }
    }

}
