package com.yuan.locationremind;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.CViewHolder
 */
public class CViewHolder extends RecyclerView.ViewHolder{

    TextView nameTv;
    TextView addressTv;
    TextView longitudeTv;
    TextView latitudeTv;
    TextView radiosTv;
    TextView intervalTv;
    ImageButton setIb;
    SwitchCompat switchBt;

    public CViewHolder(View itemView) {
        super(itemView);
        nameTv = (TextView) itemView.findViewById(R.id.remindName);
        addressTv = (TextView) itemView.findViewById(R.id.remindAddressTv);
        longitudeTv = (TextView) itemView.findViewById(R.id.remindLongitudeTv);
        latitudeTv = (TextView) itemView.findViewById(R.id.remindLatitudeTv);
        radiosTv = (TextView) itemView.findViewById(R.id.remindRadiusTv);
        intervalTv = (TextView) itemView.findViewById(R.id.remindRefreshIntervalTv);
        setIb = (ImageButton) itemView.findViewById(R.id.remindSetIb);
        switchBt = (SwitchCompat) itemView.findViewById(R.id.switchLocation);
    }
}
