package com.example.termproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity implements OnItemListener {
    TextView monthTextView;
    TextView yearTextView;
    LocalDate selectDate;
    RecyclerView calendarView;
    RecyclerView scheduleListView;
    DBHelper helper;
    SQLiteDatabase db;
    ArrayList<Schedule> scheduleOfMonth;
    ArrayList<Schedule> scheduleOfDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        helper = DBHelper.getInstance(this);
        try{
            db=helper.getWritableDatabase();
        }catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }

        LinearLayout calendarViewFrame = findViewById(R.id.LinearLayout_calender);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_calender, calendarViewFrame, false);
        calendarViewFrame.addView(v);

        monthTextView = findViewById(R.id.txt_month);
        yearTextView = findViewById(R.id.txt_year);
        ImageButton leftButton = findViewById(R.id.btn_left);
        ImageButton rightButton = findViewById(R.id.btn_right);
        ImageButton addScheduleButton = findViewById(R.id.btn_add);
        calendarView = findViewById(R.id.calenderView);
        scheduleListView = findViewById(R.id.scheduleListView);

        selectDate = LocalDate.now();
        scheduleOfMonth = new ArrayList<Schedule>();
        scheduleOfDay = new ArrayList<Schedule>();
        setMonthView();

        //???????????????
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate = selectDate.minusMonths(1);
                setMonthView();
            }
        });
        //???????????????
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate = selectDate.plusMonths(1);
                setMonthView();
            }
        });
        //????????????
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleActivity.this,addScheduleActivity.class);
                intent.putExtra("selectDate",selectDate);
                startActivity(intent);
            }
        });

    }
    //?????????
    @Override
    protected void onRestart(){
        setMonthView();
        printScheduleList();
        super.onRestart();
    }

    private ArrayList<Schedule> callDBSchedule(LocalDate s){
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();
        String queryStr = "SELECT _id,title,startdate,enddate FROM schedule WHERE date(startdate) >= date(?) " +
                "OR date(enddate) >= date(?) ";
        String startDateStringFormat = s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String[] whereArgs = new String[] {startDateStringFormat,startDateStringFormat};
        try(Cursor cursor = db.rawQuery(queryStr,whereArgs)) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                LocalDateTime startDateTime = LocalDateTime.parse(cursor.getString(2),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime endDateTime = LocalDateTime.parse(cursor.getString(3),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                schedules.add(new Schedule(id,title,startDateTime,endDateTime));
            }
        }
        return schedules;
    }
    //?????? ?????? ??? ???
    private Map<String,String> monthYearFromDate(LocalDate date){
        Map<String,String> yearMonth = new HashMap<>();
        yearMonth.put("year",date.format(DateTimeFormatter.ofPattern("yyyy???")));
        yearMonth.put("month",date.format(DateTimeFormatter.ofPattern("MM???")));
        return yearMonth;
    }
    //????????????
    private void setMonthView(){
        Map yearMonth = monthYearFromDate(selectDate);
        monthTextView.setText(yearMonth.get("month").toString());
        yearTextView.setText(yearMonth.get("year").toString());
        ArrayList<cell> dayList = dayInMonthArray(selectDate);
        CalendarAdapter adapter = new CalendarAdapter(dayList,ScheduleActivity.this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(),7);
        calendarView.setLayoutManager(manager);
        calendarView.setAdapter(adapter);
    }
    //?????????????????????
    private ArrayList<cell> dayInMonthArray(LocalDate date){
        ArrayList<cell> dayList = new ArrayList<>();
        //?????? ??????
        LocalDate firstDay = date.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue();
        //?????? ????????????
        LocalDate calendarStartDate = firstDay.minusDays(dayOfWeek);

        //DB????????????
        if(!scheduleOfMonth.isEmpty()){
            scheduleOfMonth.clear();
        }
        scheduleOfMonth = callDBSchedule(calendarStartDate);

        //?????? ?????????
        for (int i=1;i<43;i++){
            boolean activation = false;
            ArrayList<Integer> reservation = new ArrayList<Integer>();
            for (Schedule sd : scheduleOfMonth){
                if(sd.includeDay(calendarStartDate.atStartOfDay())){
                    reservation.add(sd.getId());
                }
            }
            if(calendarStartDate.getMonthValue()==date.getMonthValue()){
                activation = true;
            }
            cell newCell = new cell(calendarStartDate.getYear(),
                    calendarStartDate.getMonthValue(),
                    calendarStartDate.getDayOfMonth(),
                    activation,
                    reservation);

            if(calendarStartDate.isEqual(date)) {
                newCell.setSelect(true);
                scheduleOfDay.clear();
                for (Schedule sc : scheduleOfMonth){
                    if(reservation.contains(sc.getId())){
                        scheduleOfDay.add(sc);
                    }
                }
                printScheduleList();
            }
            dayList.add(newCell);

            calendarStartDate = calendarStartDate.plusDays(1);
        }
        return dayList;
    }

    //?????? ????????? ??????
    public void printScheduleList(){
        ScheduleListAdapter adapter = new ScheduleListAdapter(scheduleOfDay,ScheduleActivity.this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        scheduleListView.setLayoutManager(manager);
        scheduleListView.setAdapter(adapter);
    }
    //?????? ?????? ?????????
    @Override
    public void onItemClick(Schedule schedule){
        Intent intent = new Intent(ScheduleActivity.this,amendScheduleActivity.class);
        intent.putExtra("id", schedule.getId());
        intent.putExtra("title", schedule.getTitle());
        intent.putExtra("startDateTime", schedule.getStartDateTime());
        intent.putExtra("endDateTime", schedule.getEndDateTime());
        startActivity(intent);
    }
    @Override
    public void onItemClick(ArrayList<Integer> reservation){
        scheduleOfDay.clear();
        for (Schedule sc : scheduleOfMonth){
            if(reservation.contains(sc.getId())){
                scheduleOfDay.add(sc);
            }
        }
        printScheduleList();
    }
    @Override
    public void onItemClick(LocalDate date){
        selectDate = date;
    }
    @Override
    public void onItemClick(LocalDate startDate,LocalDate endDate){}
}
