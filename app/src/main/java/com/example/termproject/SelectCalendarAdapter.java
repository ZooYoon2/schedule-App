package com.example.termproject;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SelectCalendarAdapter extends RecyclerView.Adapter<SelectCalendarAdapter.SelectCalendarViewHolder>{
    ArrayList<cell> dayList;
    OnItemListener onItemListener;
    LocalDate startDate;
    LocalDate endDate;
    String selectOption;
    List<LinearLayout> calenderCellList = new ArrayList<>();
    List<TextView> dayTextViewList = new ArrayList<>();

    public SelectCalendarAdapter(LocalDate startDate, LocalDate endDate, String selectOption, ArrayList<cell> dayList, OnItemListener onItemListener){
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayList = dayList;
        this.selectOption = selectOption;
        this.onItemListener = onItemListener;
    }

    @Override
    public SelectCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_select_calender_cell,parent,false);
        return new SelectCalendarViewHolder(view);
    }
    public void setCalenderCell(){
        int idx=0;
        for(LinearLayout cell : calenderCellList){
            cell.setBackgroundResource(0);
            cell.findViewById(R.id.txt_selectdate).setBackgroundColor(0);
            TextView v = cell.findViewById(R.id.txt_selectdate);
            if ((idx + 1) % 7 == 0){
                v.setTextColor(Color.BLUE);
            }
            else if(idx==0||idx%7==0){
                v.setTextColor(Color.RED);
            }
            else{
                v.setTextColor(Color.BLACK);
            }
            if( (dayList.get(idx).getDate().isAfter(startDate) || dayList.get(idx).getDate().isEqual(startDate)) &&
                    (dayList.get(idx).getDate().isEqual(endDate) || dayList.get(idx).getDate().isBefore(endDate)) ){
                dayTextViewList.get(idx).setBackgroundColor(Color.parseColor("#6200EE"));
                dayTextViewList.get(idx).setTextColor(Color.WHITE);
            }
            idx++;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectCalendarViewHolder holder, int position){
        holder.dayText.setText(dayList.get(position).day.toString());
        if (!calenderCellList.contains(holder.calenderCell)) {
            calenderCellList.add(holder.calenderCell);
            dayTextViewList.add(holder.dayText);
        }
        if ((position + 1) % 7 == 0){
            holder.dayText.setTextColor(Color.BLUE);
        }
        else if(position==0||position%7==0){
            holder.dayText.setTextColor(Color.RED);
        }
        if (!dayList.get(position).activation){
            holder.calenderCell.setAlpha(0.5F);
        }
        if((dayList.get(position).getDate().isAfter(startDate) || dayList.get(position).getDate().isEqual(startDate)) &&
                ((dayList.get(position).getDate().isEqual(endDate) || dayList.get(position).getDate().isBefore(endDate)))){
            holder.dayText.setBackgroundColor(Color.parseColor("#6200EE"));
            holder.dayText.setTextColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dayList.get(position).activation){
                    if (selectOption=="start"){
                        if(dayList.get(position).getDate().isAfter(endDate)){
                            startDate = dayList.get(position).getDate();
                            endDate = startDate;
                            onItemListener.onItemClick(startDate,endDate);
                        }
                        else{
                            startDate = dayList.get(position).getDate();
                            onItemListener.onItemClick(startDate);
                        }
                    }
                    else{
                        if(dayList.get(position).getDate().isBefore(startDate)){
                            endDate = startDate;
                            onItemListener.onItemClick(startDate,endDate);
                        }
                        else{
                            endDate = dayList.get(position).getDate();
                            onItemListener.onItemClick(endDate);
                        }
                    }
                    setCalenderCell();
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return dayList.size();
    }

    class SelectCalendarViewHolder extends RecyclerView.ViewHolder{
        TextView dayText;
        LinearLayout calenderCell;
        public SelectCalendarViewHolder(@NonNull View itemView){
            super(itemView);
            calenderCell = itemView.findViewById(R.id.selectCalenderCell);
            dayText = itemView.findViewById(R.id.txt_selectdate);
        }
    }
}