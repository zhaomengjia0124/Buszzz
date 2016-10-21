package com.yuan.locationremind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
        menu.add(1, 1, 1, "查询").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClick(View view) {

        String la = mLatitudeEt.getText().toString();
        String lo = mLongitudeEt.getText().toString();
        setResult(RESULT_OK);
        finish();
    }
}
