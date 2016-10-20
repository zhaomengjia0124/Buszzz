package com.yuan.locationremind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 20/10/2016:3:11 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationList
 */

public class LocationListActivity extends AppCompatActivity{

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LocationAdapter adapter = new LocationAdapter(this);
        mRecyclerView.setAdapter(adapter);

    }
}
