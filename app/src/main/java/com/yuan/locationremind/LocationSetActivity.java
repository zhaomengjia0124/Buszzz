package com.yuan.locationremind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 21/10/2016:1:06 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationSetActivity
 */

public class LocationSetActivity extends AppCompatActivity {

    @BindView(R.id.setIntervalEt)
    EditText mIntervalEt;

    @BindView(R.id.setRadiusEt)
    EditText mRadiusEt;

    private LocationEntity mLocationEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        mLocationEntity = (LocationEntity) intent.getSerializableExtra("entity");
        if (mLocationEntity != null) {
            int interval = mLocationEntity.getInterval();
            float radius = mLocationEntity.getRadius();

            mIntervalEt.setText(String.valueOf(interval));
            mRadiusEt.setText(String.valueOf(radius));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.setting);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "保存").setIcon(R.mipmap.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(2, 1, 1, "删除").setIcon(R.mipmap.ic_del).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getGroupId()) {
            case 1:
                saveSets();
                break;
            case 2:
                delLocation();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void delLocation() {
        LocationDao dao = new LocationDao(this);
        dao.delete(mLocationEntity);

        setResult(RESULT_OK);
        finish();
    }

    private boolean saveSets() {
        String interval = mIntervalEt.getText().toString();
        if(TextUtils.isEmpty(interval)){
            Toast.makeText(this, "间隔不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }

        String radius = mRadiusEt.getText().toString();
        if(TextUtils.isEmpty(radius)){
            Toast.makeText(this, "半径不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }

        mLocationEntity.setRadius(Float.valueOf(radius));
        mLocationEntity.setInterval(Integer.valueOf(interval));

        LocationDao dao = new LocationDao(this);
        dao.update(mLocationEntity);

        setResult(RESULT_OK);
        finish();
        return false;
    }
}
