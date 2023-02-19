package com.example.termproject;

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

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{
    ArrayList<cell> dayList;
    OnItemListener onItemListener;
    Cursor cursor;
    List<LinearLayout> calenderCellList = new ArrayList<>();

    public CalendarAdapter(ArrayList<cell> dayList, OnItemListener onItemListener){
        this.dayList = dayList;
        this.onItemListener = onItemListener;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_calender_cell,parent,false);
        return new CalendarViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position){
        holder.dayText.setText(dayList.get(position).day.toString());
        if (!calenderCellList.contains(holder.calenderCell)) {
            calenderCellList.add(holder.calenderCell);
        }
        if ((position + 1) % 7 == 0){
            holder.dayText.setTextColor(Color.BLUE);
        }
        else if(position==0||position%7==0){
            holder.dayText.setTextColor(Color.RED);
        }
        if (!dayList.get(position).activation){
            //holder.dayText.setTextColor(Color.alpha(99));
            holder.calenderCell.setAlpha(0.5F);
        }
        if(!dayList.get(position).reservation.isEmpty()){
            holder.highlightView.setBackgroundColor(Color.RED);
        }
        if(dayList.get(position).select){
            holder.calenderCell.setBackgroundResource(R.drawable.border);
            holder.dayText.setBackgroundColor(Color.parseColor("#6200EE"));
            holder.dayText.setTextColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dayList.get(position).activation){
                    int idx = 0;
                    for(LinearLayout cell : calenderCellList){
                        cell.setBackgroundResource(0);
                        cell.findViewById(R.id.txt_selectdate).setBackgroundColor(0);
                        TextView v = cell.findViewById(R.id.txt_selectdate);
                        v.setTextColor(Color.BLACK);
                        if ((idx + 1) % 7 == 0){
                            v.setTextColor(Color.BLUE);
                        }
                        else if(idx==0||idx%7==0){
                            v.setTextColor(Color.RED);
                        }
                        else{
                            v.setTextColor(Color.BLACK);
                        }
                        idx++;
                    }
                    holder.calenderCell.setBackgroundResource(R.drawable.border);
                    holder.dayText.setBackgroundColor(Color.parseColor("#6200EE"));
                    holder.dayText.setTextColor(Color.WHITE);
                    onItemListener.onItemClick(dayList.get(position).getDate());
                    onItemListener.onItemClick(dayList.get(position).getReservation());
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{
        TextView dayText;
        List<LinearLayout> calenderCellList = new ArrayList<>();
        LinearLayout calenderCell;
        ImageView highlightView;
        public CalendarViewHolder(@NonNull View itemView){
            super(itemView);
            calenderCell = itemView.findViewById(R.id.selectCalenderCell);
            dayText = itemView.findViewById(R.id.txt_selectdate);
            highlightView = itemView.findViewById(R.id.highlightView);
        }
    }
}
