package com.yuan.locationremind;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 20/10/2016:3:11 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationList
 */

public class LocationListActivity extends CheckPermissionsActivity {

    public static int REQUEST_CODE = 1;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.resultInfoTv)
    TextView mResultTv;
    boolean bBackPressed;
    private LocationDao mLocationDao;
    private LocationAdapter mAdapter;
    private AMapLocation mLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_location_list);
        ButterKnife.bind(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.logo);
        setSupportActionBar(toolbar);

        mLocationDao = new LocationDao(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LocationAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        handleState(savedInstanceState);

    }

    private void handleState(@Nullable Bundle savedInstanceState) {
        if (!isServiceWork(this, "com.yuan.locationremind.LocationService")) {
            for (LocationEntity entity : mLocationDao.queryAll()) {
                if (entity.getSelected() == 1) {
                    entity.setSelected(0);
                    mLocationDao.update(entity);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "").setIcon(R.mipmap.ic_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivityForResult(new Intent(LocationListActivity.this, LocationAddActivity.class), REQUEST_CODE);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (getCurrentRemind() != null) {
            EventBus.getDefault().post(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshView(LocationService service) {
        List<LocationEntity> list = mLocationDao.queryAll();
        mAdapter.refresh(list);
        if (getCurrentRemind() == null) {
            mResultTv.setText("没有定位");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(AMapLocation location) {
        mLocation = location;
        StringBuilder sb = new StringBuilder();
        sb.append("经度：").append(location.getLongitude()).append("  ");
        sb.append("纬度：").append(location.getLatitude()).append("\n");
        sb.append("位置：").append(location.getAddress());
        mResultTv.setText(sb);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<LocationEntity> list = mLocationDao.queryAll();
            mAdapter.refresh(list);
        }
    }

    /**
     * 如果有可能被回收，则将状态重置
     *
     * @param outState bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("location", mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        handleState(savedInstanceState);
        refreshView(null);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 得到当前开启提醒
     */
    private LocationEntity getCurrentRemind() {
        for (LocationEntity e : mLocationDao.queryAll()) {
            if (e.getSelected() == 1) {
                return e;
            }
        }

        return null;
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bBackPressed) {
            for (LocationEntity entity : mLocationDao.queryAll()) {
                if (entity.getSelected() == 1) {
                    entity.setSelected(0);
                    mLocationDao.update(entity);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        bBackPressed = true;
        super.onBackPressed();
    }
}

