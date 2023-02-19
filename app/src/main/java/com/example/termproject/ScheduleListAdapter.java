package com.example.termproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ScheduleListViewHolder>{
    ArrayList<Schedule> scheduleList;
    OnItemListener onItemListener;

    public ScheduleListAdapter(ArrayList<Schedule> scheduleList, OnItemListener onItemListener){
        this.scheduleList = scheduleList;
        this.onItemListener = onItemListener;
    }

    @Override
    public ScheduleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_schedulelist,parent,false);
        return new ScheduleListViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ScheduleListViewHolder holder, int position){
        holder.title.setText(scheduleList.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemListener.onItemClick(scheduleList.get(position));
            }
        });
    }

    @Override
    public int getItemCount(){
        return scheduleList.size();
    }

    class ScheduleListViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView important;
        public ScheduleListViewHolder(@NonNull View itemView){
            super(itemView);
            important = itemView.findViewById(R.id.colorByIpt);
            title = itemView.findViewById(R.id.txt_title);
        }
    }
}
