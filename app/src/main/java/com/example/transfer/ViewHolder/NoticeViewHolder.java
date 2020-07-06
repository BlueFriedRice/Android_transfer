package com.example.transfer.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.transfer.Interface.ItemClickListener;
import com.example.transfer.R;

public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView transText;
    public TextView dateText;
    public TextView timeText;
    public TextView aftetimeText;
    public ImageButton delete;
    public ImageButton start;
    public ItemClickListener listner;

    public NoticeViewHolder(View itemView) {
        super(itemView);

        transText = itemView.findViewById(R.id.transText);
        dateText = itemView.findViewById(R.id.dateText);
        timeText = itemView.findViewById(R.id.timeText);
        aftetimeText = itemView.findViewById(R.id.aftertimeText);
        delete = itemView.findViewById(R.id.delete);
        start = itemView.findViewById(R.id.btnStart);
    }

    public void setItemClickListner(ItemClickListener listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
