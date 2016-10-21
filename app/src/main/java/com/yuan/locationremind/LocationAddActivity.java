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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.searchBar.MaterialSearchBar;
import com.yuan.locationremind.sqlite.LocationDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 20/10/2016:1:03 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationPointActivity
 */

public class LocationAddActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener, Inputtips.InputtipsListener {

    @BindView(R.id.latitudeEt)
    EditText mLatitudeEt;

    @BindView(R.id.longitudeEt)
    EditText mLongitudeEt;

    @BindView(R.id.searchBar)
    MaterialSearchBar searchBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);
        ButterKnife.bind(this);

        searchBar.setOnSearchActionListener(this);
        searchBar.setText("111");
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

        LocationDao dao = new LocationDao(this);
        dao.insert(entity);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        Toast.makeText(this, "StateChanged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        Toast.makeText(this, "ButtonClicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTextChange(CharSequence text) {
        InputtipsQuery inputQuery = new InputtipsQuery(text.toString(), "");
        inputQuery.setCityLimit(true);


        Inputtips inputTips = new Inputtips(this, inputQuery);
        inputTips.setInputtipsListener(this);

        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        List<String> addressList = new ArrayList<>();
        for (Tip tip : list) {
            addressList.add(tip.getName());
        }
        searchBar.setLastSuggestions(addressList);
    }
}
