package com.yuan.locationremind;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuan.locationremind.entity.LocationEntity;

import java.util.List;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationAdatper
 */

public class LocationAdapter extends RecyclerView.Adapter<CViewHolder> {

    private Context mContext;

    private List<LocationEntity> mDataList;

    public LocationAdapter(Context context) {
        mContext = context;
    }

    public void refresh(@NonNull List<LocationEntity> list) {
        mDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public CViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.location_list_item, parent, false);
        return new CViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
