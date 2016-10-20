package com.yuan.locationremind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;


/**
 * Created by Yuan on 19/10/2016:6:09 PM.
 * <p/>
 * Description:com.yuan.locationremind.MainActivity
 */
public class MainActivity extends CheckPermissionsActivity implements AMapLocationListener {

    private TextView mLocationResultTv;

    private AMapLocationClient mLocationClient;

    private AMapLocationClientOption mLocationOption;

    private PendingIntent mAlarmPendingIntent = null;

    private AlarmManager mAlarmManager = null;

    private Vibrator mVibrator;

    private boolean bEnable;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                //开始定位
                case Utils.MSG_LOCATION_START:
                    mLocationResultTv.setText("状况    :正在定位...");
                    break;
                // 定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    StringBuilder sb = new StringBuilder();
                    sb.append("状况    :定位成功" + "\n");
                    sb.append("类型    : ").append(loc.getLocationType()).append("\n");
                    sb.append("经度    : ").append(loc.getLongitude()).append("\n");
                    sb.append("纬度    : ").append(loc.getLatitude()).append("\n");
                    sb.append("精度    : ").append(loc.getAccuracy()).append("米").append("\n");

                    mLocationResultTv.setText(sb);
                    break;
                //停止定位
                case Utils.MSG_LOCATION_STOP:
                    mLocationResultTv.setText("状况    :停止定位");
                    break;
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LOCATION")) {
                if (null != mLocationClient) {
                    mLocationClient.startLocation();
                }
            }
        }
    };
    //自定义广播接收器
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int status = bundle.getInt("event");
            if (status == 1) {
                vibrate();
                Toast.makeText(MainActivity.this, "到站了", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Switch vibrateSwitch = (Switch) findViewById(R.id.vibrateSwitch);
        vibrateSwitch.setChecked((Boolean) SharedPreferenceHelper.getData(MainActivity.this, "enable", false));
        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bEnable = isChecked;
                SharedPreferenceHelper.putData(MainActivity.this, "enable", bEnable);
                handleLocation();
            }
        });
        handleLocation();
    }

    private void handleLocation() {

        if (!bEnable) {
            return;
        }


        String latitude = (String) SharedPreferenceHelper.getData(this, "latitude", "");
        String longitude = (String) SharedPreferenceHelper.getData(this, "longitude", "");

        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
            Toast.makeText(this, "请设置经纬度！", Toast.LENGTH_SHORT).show();
            return;
        }

        String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
        IntentFilter fliter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        fliter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, fliter);
        Intent intent = new Intent(GEOFENCE_BROADCAST_ACTION);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        mLocationResultTv = (TextView) findViewById(R.id.resultTv);

        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        mLocationClient.setLocationListener(this);

        // 39.978578, 116.352245
        mLocationClient.addGeoFenceAlert("fenceId", Double.valueOf(latitude), Double.valueOf(longitude), 100, -1, mPendingIntent);

        // 创建Intent对象，action为LOCATION
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("LOCATION");

        mAlarmPendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        registerReceiver(mAlarmReceiver, filter);

        mLocationOption.setNeedAddress(true);
        mLocationOption.setGpsFirst(false);
        mLocationOption.setInterval(2000);
        int alarmInterval = 10;
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);
        if (null != mAlarmManager) {
            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2 * 1000, alarmInterval * 1000, mAlarmPendingIntent);
        }
    }

    public void onClick(View view) {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }

        if (null != mAlarmReceiver) {
            unregisterReceiver(mAlarmReceiver);
            mAlarmReceiver = null;
        }
        if (null != mGeoFenceReceiver) {
            unregisterReceiver(mGeoFenceReceiver);
        }
        if (null != mAlarmManager) {
            mAlarmManager.cancel(mAlarmPendingIntent);
        }

        if (null != mVibrator) {
            mVibrator.cancel();
        }
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    private void vibrate() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 200, 100, 200, 100, 300, 100, 400, 100, 500, 100, 600};
        mVibrator.vibrate(pattern, pattern.length / 2);
    }


    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = Utils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    public void onSet(View view) {
        if (mVibrator != null) {
            mVibrator.cancel();
        }
        startActivityForResult(new Intent(MainActivity.this, LocationListActivity.class), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            handleLocation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferenceHelper.putData(MainActivity.this, "enable", false);
        super.onSaveInstanceState(outState);
    }
}
