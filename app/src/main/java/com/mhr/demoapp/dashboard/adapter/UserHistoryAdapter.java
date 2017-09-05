package com.mhr.demoapp.dashboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhr.demoapp.R;
import com.mhr.demoapp.dashboard.model.UserHistory;

import java.util.List;


/**
 * Created by Mihir on 4/28/2017.
 */

public class UserHistoryAdapter extends RecyclerView.Adapter<UserHistoryAdapter.UserHistoryViewHolder> {

    private List<UserHistory> userHistoryList;
    private int rowLayout;
    private Context mContext;

    public UserHistoryAdapter(List<UserHistory> userHistoryList, int rowLayout, Context context) {
        this.userHistoryList = userHistoryList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public UserHistoryAdapter.UserHistoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new UserHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserHistoryViewHolder holder, final int position) {
        holder.txtDateTime.setText(userHistoryList.get(position).loginDateTime + "");
        holder.txtLatitude.setText(userHistoryList.get(position).loginlatitude + "");
        holder.txtLongitude.setText(userHistoryList.get(position).loginlongitude + "");
    }

    @Override
    public int getItemCount() {
        return userHistoryList.size();
    }

    public static class UserHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtDateTime;
        TextView txtLatitude;
        TextView txtLongitude;

        public UserHistoryViewHolder(View v) {
            super(v);
            txtDateTime = (TextView) v.findViewById(R.id.txtDateTime);
            txtLatitude = (TextView) v.findViewById(R.id.txtLatitude);
            txtLongitude = (TextView) v.findViewById(R.id.txtLongitude);
        }
    }
}
