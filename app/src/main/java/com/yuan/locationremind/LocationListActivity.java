package com.yuan.locationremind;

import android.app.Activity;
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

    private LocationDao mLocationDao;

    private LocationAdapter mAdapter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.location_list);
        setContentView(R.layout.activity_location_list);
        ButterKnife.bind(this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mLocationDao = new LocationDao(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LocationAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        List<LocationEntity> list = mLocationDao.queryAll();
        mAdapter.refresh(list);
        mResultTv.setText("没有定位");

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
        getLatestLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationHandle(boolean operation) {
        if (operation) {
            mToolbar.setSubtitle(R.string.location_start);
        } else {
            mToolbar.setSubtitle(R.string.location_stop);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(AMapLocation location) {
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
     * 如果被回收，则将状态重置
     *
     * @param outState bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        LocationDao dao = new LocationDao(this);

        for (LocationEntity entity : dao.queryAll()) {
            if (entity.getSelected() == 1) {
                entity.setSelected(0);
                mLocationDao.update(entity);
            }
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Activity被关闭，而service还在运行，再次再开Activity时，则定位状态会显示没有定位
     * 从数据库里拿出数据，判断如果有定位，则通知service 拿着最后一次定位的信息，回调一次刷新
     */
    private void getLatestLocation() {
        LocationDao dao = new LocationDao(this);
        for (LocationEntity entity : dao.queryAll()) {
            if (entity.getSelected() == 1) {
                EventBus.getDefault().post(this);
            }
        }
    }
}

