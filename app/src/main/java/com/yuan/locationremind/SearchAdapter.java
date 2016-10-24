package com.yuan.locationremind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuan.locationremind.entity.LocationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationAdatper
 */

public class SearchAdapter extends RecyclerView.Adapter<SViewHolder> {

    private Context mContext;

    private List<LocationEntity> mDataList = new ArrayList<>();

    OnItemClickListener mListener;


    public SearchAdapter(Context context) {
        mContext = context;
    }

    public void refresh(List<LocationEntity> list) {
        mDataList = list == null ? Collections.<LocationEntity>emptyList() : list;
        notifyDataSetChanged();
    }

    @Override
    public SViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.search_list_item, parent, false);
        return new SViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SViewHolder holder, int position) {
        final LocationEntity entity = mDataList.get(position);
        holder.addressTv.setText(entity.getAddress());
        holder.addressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(entity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    interface OnItemClickListener {
        void onItemClick(LocationEntity entity);
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }


}
