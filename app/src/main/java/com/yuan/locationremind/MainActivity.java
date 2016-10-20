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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Yuan on 19/10/2016:6:09 PM.
 * <p/>
 * Description:com.yuan.locationremind.MainActivity
 */
public class MainActivity extends CheckPermissionsActivity implements AMapLocationListener {

    @BindView(R.id.resultTv)
    TextView mLocationResultTv;

    //http://restapi.amap.com/v3/geocode/geo?key=389880a06e3f893ea46036f030c94700&address=%E8%A5%BF%E5%9C%9F%E5%9F%8E

    private AMapLocationClient mLocationClient;

    private AMapLocationClientOption mLocationOption;

    private PendingIntent mAlarmPendingIntent = null;

    private AlarmManager mAlarmManager = null;

    private Vibrator mVibrator;

    private Handler mHandler = new Handler() {
        @Override
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
                vibrate();
                Toast.makeText(MainActivity.this, "到站了", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

    }

    private void init() {

        initAlarm();

        registerLocationReceiver();
        registerAlarmReceiver();

        initLocationClient();
    }

    private void initLocationClient() {

        String latitude = (String) SharedPreferenceHelper.getData(this, "latitude", "");
        String longitude = (String) SharedPreferenceHelper.getData(this, "longitude", "");

        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
            Toast.makeText(this, "请设置经纬度！", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent("com.location.buszzz.broadcast");
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationClient.addGeoFenceAlert("fenceId", Double.valueOf(latitude), Double.valueOf(longitude), 100, -1, mPendingIntent);// 39.978578, 116.352245
        mLocationClient.setLocationListener(this);
        initLocationOption();
        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();
        mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);
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

    private void registerAlarmReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.location.buszzz.alarm");
        registerReceiver(mAlarmReceiver, filter);
    }

    private void initLocationOption() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setGpsFirst(false);
        mLocationOption.setInterval(2000);
    }

    private void registerLocationReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("com.location.buszzz.broadcast");
        registerReceiver(mLocationReceiver, filter);
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
        if (null != mLocationReceiver) {
            unregisterReceiver(mLocationReceiver);
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
            init();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferenceHelper.putData(MainActivity.this, "enable", false);
        super.onSaveInstanceState(outState);
    }
}
