package com.yuan.locationremind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Yuan on 20/10/2016:1:03 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationPointActivity
 */

public class LocationPointActivity extends AppCompatActivity {

    private EditText mLatitudeEt;

    private EditText mLongitudeEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);

        mLatitudeEt = (EditText) findViewById(R.id.latitudeEt);
        mLongitudeEt = (EditText) findViewById(R.id.longitudeEt);
        String latitude = (String) SharedPreferenceHelper.getData(this, "latitude", "");
        String longitude = (String) SharedPreferenceHelper.getData(this, "longitude", "");

        mLatitudeEt.setText(latitude);
        mLongitudeEt.setText(longitude);

    }

    public void onClick(View view) {

        String la = mLatitudeEt.getText().toString();
        String lo = mLongitudeEt.getText().toString();

        SharedPreferenceHelper.putData(this, "latitude", la);
        SharedPreferenceHelper.putData(this, "longitude", lo);

        setResult(RESULT_OK);

        finish();
    }
}
