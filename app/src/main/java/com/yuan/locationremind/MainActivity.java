package com.yuan.locationremind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yuan.locationremind.entity.LocationEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Yuan on 19/10/2016:6:09 PM.
 * <p/>
 * Description:com.yuan.locationremind.MainActivity
 */
public class MainActivity extends CheckPermissionsActivity {

    @BindView(R.id.locationResultTv)
    TextView mLocationResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startService(new Intent(this, LocationService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "更改").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(MainActivity.this, LocationListActivity.class));
        return super.onOptionsItemSelected(item);
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
        if(operation) {
            mLocationResultTv.setText(R.string.location_start);
        }else{
            mLocationResultTv.setText(R.string.location_stop);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationEntity event){
        mLocationResultTv.setText(event.toString());
    }

    public void onSet(View view) {
        startActivityForResult(new Intent(MainActivity.this, LocationListActivity.class), 100);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferenceHelper.putData(MainActivity.this, "enable", false);
        super.onSaveInstanceState(outState);
    }
}
