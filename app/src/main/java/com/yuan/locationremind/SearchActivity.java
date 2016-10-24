package com.yuan.locationremind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.yuan.locationremind.entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class SearchActivity extends AppCompatActivity implements Inputtips.InputtipsListener, SearchAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @BindView(R.id.searchEt)
    ClearEditText mEditText;

    SearchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mAdapter = new SearchAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setListener(this);
    }

    @OnTextChanged(R.id.searchEt)
    public void update() {
        mEditText.updateIconClear();

        InputtipsQuery inputQuery = new InputtipsQuery(mEditText.getText().toString(), "");
        inputQuery.setCityLimit(true);

        Inputtips inputTips = new Inputtips(this, inputQuery);
        inputTips.setInputtipsListener(this);

        inputTips.requestInputtipsAsyn();
    }


    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        List<LocationEntity> addressList = new ArrayList<>();
        for (Tip tip : list) {
            LocationEntity entity = new LocationEntity();
            entity.setLatitude(tip.getPoint().getLatitude());
            entity.setLongitude(tip.getPoint().getLongitude());
            entity.setAddress(tip.getName());
            addressList.add(entity);
        }
        mAdapter.refresh(addressList);
    }


    @Override
    public void onItemClick(LocationEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("entity", entity);
        setResult(RESULT_OK, intent);
        finish();
    }
}
