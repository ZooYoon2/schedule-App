package com.example.termproject;

import java.time.LocalDate;
import java.util.ArrayList;

public interface OnItemListener {
    void onItemClick(Schedule schedule);
    void onItemClick(ArrayList<Integer> reservation);
    void onItemClick(LocalDate Date);
    void onItemClick(LocalDate startDate,LocalDate endDate);
}
