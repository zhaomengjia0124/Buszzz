package com.yuan.locationremind;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Yuan on 20/10/2016:3:25 PM.
 * <p/>
 * Description:com.yuan.locationremind.CViewHolder
 */
public class CViewHolder extends RecyclerView.ViewHolder{

    private TextView addressTv;

    private TextView longitude;

    private TextView latitude;


    public CViewHolder(View itemView) {
        super(itemView);

    }
}
