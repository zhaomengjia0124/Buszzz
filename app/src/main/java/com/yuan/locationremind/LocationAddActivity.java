package com.yuan.locationremind;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 20/10/2016:1:03 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationPointActivity
 */

public class LocationAddActivity extends AppCompatActivity {

    @BindView(R.id.latitudeEt)
    EditText mLatitudeEt;

    @BindView(R.id.longitudeEt)
    EditText mLongitudeEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_location);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "查询").setIcon(R.mipmap.ic_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(2, 2, 1, "添加").setIcon(R.mipmap.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:

                break;
            case 2:
                saveLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLocation() {
        String latitude = mLatitudeEt.getText().toString();
        String longitude = mLongitudeEt.getText().toString();
        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
            Toast.makeText(this, "经度或纬度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        double la = Double.parseDouble(latitude);
        double lo = Double.parseDouble(longitude);
        if (la > 360 || la < 0) {
            Toast.makeText(this, "纬度超出范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lo > 360 || lo < 0) {
            Toast.makeText(this, "经度超出范围", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationEntity entity = new LocationEntity();
        entity.setLatitude(la);
        entity.setLongitude(lo);

        LocationDao dao=new LocationDao(this);
        dao.insert(entity);
        setResult(RESULT_OK);
        finish();
    }
}
