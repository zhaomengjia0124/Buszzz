package com.yuan.locationremind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 20/10/2016:3:11 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationList
 */

public class LocationListActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 1;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private LocationDao mLocationDao;

    private LocationAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.location_list);
        setContentView(R.layout.activity_location_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocationDao = new LocationDao(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LocationAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.refresh(getDatas());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "新建").setIcon(R.mipmap.ic_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivityForResult(new Intent(LocationListActivity.this, LocationAddActivity.class), REQUEST_CODE);
        return super.onOptionsItemSelected(item);
    }


    public List<LocationEntity> getDatas() {

        List<LocationEntity> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LocationEntity entity = new LocationEntity();
            entity.setAddress("Address");
            entity.setLatitude(191);
            entity.setLongitude(78);
            list.add(entity);
        }
        return list;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationHandle(boolean operation) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationEntity event) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<LocationEntity> list = mLocationDao.queryAll();
            mAdapter.refresh(list);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferenceHelper.putData(LocationListActivity.this, "enable", false);
        super.onSaveInstanceState(outState);
    }
}
