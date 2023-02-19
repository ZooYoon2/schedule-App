package com.example.termproject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class cell{
    public Integer year;
    public Integer month;
    public Integer day;
    public boolean activation;
    public boolean select = false;
    public ArrayList<Integer> reservation;
    cell(Integer year, Integer month, Integer day, boolean activation){
        this.year = year;
        this.month = month;
        this.day = day;
        this.activation = activation;
    }
    cell(Integer year, Integer month, Integer day, boolean activation,ArrayList<Integer> reservation){
        this.year = year;
        this.month = month;
        this.day = day;
        this.activation = activation;
        this.reservation = reservation;
    }
    public void setSelect(boolean select){this.select = select;}
    public LocalDate getDate(){
        String std = String.format("%02d-%02d-%02d",year,month,day);
        LocalDate date = LocalDate.parse(std, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return date;
    }
    public ArrayList<Integer> getReservation(){return reservation;}
}
