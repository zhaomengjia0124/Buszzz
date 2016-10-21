package com.yuan.locationremind;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.CViewHolder
 */
public class SViewHolder extends RecyclerView.ViewHolder {

    TextView addressTv;


    public SViewHolder(View itemView) {
        super(itemView);
        addressTv = (TextView) itemView.findViewById(R.id.searchListItem);

    }
}
