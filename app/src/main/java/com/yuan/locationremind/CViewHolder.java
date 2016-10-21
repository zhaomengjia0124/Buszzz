package com.yuan.locationremind;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.CViewHolder
 */
public class CViewHolder extends RecyclerView.ViewHolder{

    TextView addressTv;
    TextView longitudeTv;
    TextView latitudeTv;
    TextView radiosTv;
    TextView intervalTv;
    ImageButton setIb;

    public CViewHolder(View itemView) {
        super(itemView);
        addressTv = (TextView) itemView.findViewById(R.id.remindAddressTv);
        longitudeTv = (TextView) itemView.findViewById(R.id.remindLongitudeTv);
        latitudeTv = (TextView) itemView.findViewById(R.id.remindLatitudeTv);
        radiosTv = (TextView) itemView.findViewById(R.id.remindRadiusTv);
        intervalTv = (TextView) itemView.findViewById(R.id.remindRefreshIntervalTv);
        setIb = (ImageButton) itemView.findViewById(R.id.remindSetIb);
    }
}
