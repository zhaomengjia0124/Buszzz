package com.yuan.locationremind;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.sqlite.LocationDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationAdatper
 */

public class LocationAdapter extends RecyclerView.Adapter<CViewHolder> {

    private Context mContext;

    private List<LocationEntity> mDataList = new ArrayList<>();

    private LocationDao mLocationDao;

    public LocationAdapter(Context context) {
        mContext = context;
        mLocationDao = new LocationDao(mContext);
    }

    public void refresh(List<LocationEntity> list) {
        mDataList = list == null ? Collections.<LocationEntity>emptyList() : list;
        notifyDataSetChanged();
    }

    @Override
    public CViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.location_list_item, parent, false);
        return new CViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CViewHolder holder, int position) {
        final LocationEntity entity = mDataList.get(position);
        holder.addressTv.setText(entity.getAddress());
        holder.latitudeTv.setText(String.valueOf(entity.getLatitude()));
        holder.longitudeTv.setText(String.valueOf(entity.getLongitude()));
        holder.intervalTv.setText(String.valueOf(entity.getInterval()));
        holder.radiosTv.setText(String.valueOf(entity.getRadius()));

        if (entity.getSelected() == 0) {
            holder.switchBt.setChecked(false);
        } else {
            holder.switchBt.setChecked(true);
        }


        holder.setIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRemind(entity);
                toLocationSetActivity(entity);
            }
        });

        holder.switchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.switchBt.isChecked()) {
                    openRemind(entity);
                } else {
                    closeRemind(entity);
                }
            }
        });

    }

    private void toLocationSetActivity(LocationEntity entity) {
        LocationListActivity activity = (LocationListActivity) mContext;
        Intent intent = new Intent();
        intent.putExtra("entity", entity);
        intent.setClass(activity, LocationSetActivity.class);
        activity.startActivityForResult(intent, LocationListActivity.REQUEST_CODE);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    private void closeRemind(LocationEntity entity) {
        entity.setSelected(0);
        mLocationDao.update(entity);

        mDataList = mLocationDao.queryAll();
        notifyDataSetChanged();
        LocationListActivity activity = (LocationListActivity) mContext;
        Intent intent = new Intent(mContext, LocationService.class);
        activity.mResultTv.setText("没有定位");
        mContext.stopService(intent);

    }

    private void openRemind(LocationEntity sEntity) {

        for (int i = 0; i < mDataList.size(); i++) {
            LocationEntity entity = mDataList.get(i);
            if (entity.getSelected() == 1) {
                entity.setSelected(0);
                mLocationDao.update(entity);
            }
        }
        sEntity.setSelected(1);
        mLocationDao.update(sEntity);

        mDataList = mLocationDao.queryAll();
        notifyDataSetChanged();

        Intent intent = new Intent(mContext, LocationService.class);
        mContext.stopService(intent);
        intent.putExtra("entity", sEntity);
        mContext.startService(intent);

    }

}
