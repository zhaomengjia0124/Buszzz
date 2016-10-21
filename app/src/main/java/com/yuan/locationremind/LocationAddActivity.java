package com.yuan.locationremind;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.textservice.SuggestionsInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yuan.locationremind.LocationListActivity.REQUEST_CODE;

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

    private Toolbar mToolbar;

    private static int REQUEST_CODE = 1;
    LocationEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "搜索").setIcon(R.mipmap.ic_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(2, 1, 1, "确定").setIcon(R.mipmap.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

                break;
            case 2:
                saveLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            entity = (LocationEntity) data.getSerializableExtra("entity");
            if (entity != null) {
                mLatitudeEt.setText(String.valueOf(entity.getLatitude()));
                mLongitudeEt.setText(String.valueOf(entity.getLongitude()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        if (entity == null) {
            entity = new LocationEntity();
        }
        entity.setLatitude(la);
        entity.setLongitude(lo);
        entity.setInterval(Constants.REFRESH_INTERVAL);
        entity.setRadius(Constants.RADIUS);

        LocationDao dao = new LocationDao(this);
        dao.insert(entity);
        setResult(RESULT_OK);
        finish();
    }


}
