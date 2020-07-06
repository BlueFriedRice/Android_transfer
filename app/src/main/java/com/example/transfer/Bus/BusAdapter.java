package com.example.transfer.Bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transfer.R;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.MyViewHolder> {

    private ArrayList<BusDataItem> mList;
    private LayoutInflater mInflate;
    private Context mContext;

    public BusAdapter(Context context, ArrayList<BusDataItem> itmes) {
        this.mList = itmes;
        this.mInflate = LayoutInflater.from(context);
        this.mContext = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.activity_busitem, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //binding
        holder.rtNm.setText(mList.get(position).rtNm);
        holder.stnNm.setText(mList.get(position).stnNm);
        holder.arrmsg1.setText(mList.get(position).arrmsg1);
        holder.arrmsg2.setText(mList.get(position).arrmsg2);

        //Click event
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rtNm;
        public TextView stnNm;
        public TextView arrmsg1;
        public TextView arrmsg2;

        public MyViewHolder(View itemView) {
            super(itemView);

            rtNm = itemView.findViewById(R.id.tv_plateNo1);
            stnNm = itemView.findViewById(R.id.tv_locationNo1);
            arrmsg1 = itemView.findViewById(R.id.tv_routeId);
            arrmsg2 = itemView.findViewById(R.id.tv_stationId);

        }
    }

}
