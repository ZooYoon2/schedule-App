package com.example.termproject;

import android.graphics.Color;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Schedule {
    int id;
    String title;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    public Schedule(int id,String title, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.id = id;
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
    public boolean includeDay(LocalDateTime d){
        if((d.toLocalDate().isAfter(startDateTime.toLocalDate()) || d.toLocalDate().isEqual(startDateTime.toLocalDate()))
                && (d.toLocalDate().isEqual(endDateTime.toLocalDate()) || d.toLocalDate().isBefore(endDateTime.toLocalDate()))){
            return true;
        }
        return false;
    }
    public String getTitle(){return title;}
    public Integer getId(){return id;}
    public LocalDateTime getStartDateTime(){return startDateTime;}
    public LocalDateTime getEndDateTime(){return endDateTime;}
}
