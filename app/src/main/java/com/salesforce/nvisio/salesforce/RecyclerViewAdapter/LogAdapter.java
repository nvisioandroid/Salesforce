package com.salesforce.nvisio.salesforce.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salesforce.nvisio.salesforce.Model.login_data;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.database.WorkdayData;

import java.util.List;

/**
 * Created by USER on 08-May-17.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<WorkdayData> listData;
    private LayoutInflater inflater;
    private Context context;

    public LogAdapter(List<WorkdayData> listData, Context context) {
        this.listData = listData;
        this.context = context;
        this.inflater=LayoutInflater.from(context);

    }


    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.log_recycler_single,parent,false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        WorkdayData data=listData.get(position);
        holder.logDate.setText(data.getPerformedDate());
        holder.logDuration.setText(data.getDurationInM());
        if (holder.logDuration.getText().toString().equals("")){
            holder.logDuration.setText("On-going");
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class LogViewHolder extends RecyclerView.ViewHolder{

        private TextView logDate, logDuration;

        public LogViewHolder(View itemView) {
            super(itemView);
            logDate= (TextView) itemView.findViewById(R.id.logDate);
            logDuration= (TextView) itemView.findViewById(R.id.logDuration);
        }
    }
}
