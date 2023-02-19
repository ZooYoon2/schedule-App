package com.example.termproject;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class addScheduleActivity extends AppCompatActivity implements OnItemListener{
    //데이터베이스
    DBHelper helper;
    SQLiteDatabase db;
    //설정시간
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    //선택시간
    LocalDateTime selectStartDateTime;
    LocalDateTime selectEndDateTime;
    String selectOption;
    //텍스트
    TextView startTextView;
    TextView endTextView;
    EditText titleTextView;
    //뷰
    RecyclerView calendarView;
    BottomSheetDialog DateDialog;
    TimePicker timePicker;
    //수정시 사용되는 데이터베이스ID
    Integer id = -1;
    //BottomSheet 버튼 날짜 설정
    void setSelectDate(){
        ((Button)DateDialog.findViewById(R.id.btn_selectStart)).setText(selectStartDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ((Button)DateDialog.findViewById(R.id.btn_selectEnd)).setText(selectEndDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
    //BottomSheet 버튼 선택효과
    void setSelect(){
        if (selectOption == "start"){
            DateDialog.findViewById(R.id.btn_selectStart).setAlpha(1.0F);
            DateDialog.findViewById(R.id.btn_selectEnd).setAlpha(0.5F);
        }
        else{
            DateDialog.findViewById(R.id.btn_selectEnd).setAlpha(1.0F);
            DateDialog.findViewById(R.id.btn_selectStart).setAlpha(0.5F);
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addschedule);

        //DB불러오기
        helper = DBHelper.getInstance(this);
        try{
            db=helper.getWritableDatabase();
        }catch (SQLiteException ex){
            db=helper.getReadableDatabase();
        }

        //뷰 연결
        startTextView = findViewById(R.id.txt_startdate);
        endTextView = findViewById(R.id.txt_enddate);
        titleTextView = findViewById(R.id.etxt_title);

        //기본값 당일로 설정
        Set<String> Keys = getIntent().getExtras().keySet();
        if(Keys.contains("selectDate")){
            startDateTime = ((LocalDate) getIntent().getExtras().get("selectDate")).atStartOfDay();
            endDateTime = (startDateTime.plusDays(1)).minusSeconds(1);
            selectStartDateTime = startDateTime;
            selectEndDateTime = endDateTime;
        }
        else{
            startDateTime = (LocalDateTime) getIntent().getExtras().get("startDateTime");
            endDateTime = (LocalDateTime) getIntent().getExtras().get("endDateTime");
            selectStartDateTime = startDateTime;
            selectEndDateTime = endDateTime;
            titleTextView.setText((String) getIntent().getExtras().get("title"));
            id = (Integer) getIntent().getExtras().get("id");
        }

        //텍스트뷰 현재 시각으로 초기화
        startTextView.setText(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        endTextView.setText(endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        //시트다이어로그
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.sheet_bottom,null);
        DateDialog = new BottomSheetDialog(this);
        DateDialog.setContentView(bottomSheetView);
        DateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                selectStartDateTime = startDateTime;
                selectEndDateTime = endDateTime;
                DateDialog.cancel();
            }
        });

        //리사이클 달력
        calendarView = DateDialog.findViewById(R.id.selectcalenderView);
        timePicker = DateDialog.findViewById(R.id.timePicker);

        //BottomSheet 시작일 변경
        DateDialog.findViewById(R.id.btn_selectStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOption = "start";
                setSelect();
                setMonthView();//달력생성
            }
        });
        //BottomSheet 종료일 변경
        DateDialog.findViewById(R.id.btn_selectEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOption = "end";
                setSelect();
                setMonthView();//달력생성
            }
        });
        //시작일변경
        findViewById(R.id.btn_startdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOption = "start";
                timePicker.setHour(selectStartDateTime.getHour());
                timePicker.setMinute(selectStartDateTime.getMinute());
                setMonthView();//달력생성
                setSelectDate();//버튼 텍스트변경
                setSelect();//버튼선택 하이라이트
                DateDialog.show();
            }
        });
        //종료일변경
        findViewById(R.id.btn_enddate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOption = "end";
                timePicker.setHour(selectEndDateTime.getHour());
                timePicker.setMinute(selectEndDateTime.getMinute());
                setMonthView();//달력생성
                setSelectDate();//버튼 텍스트변경
                setSelect();//버튼선택 하이라이트
                DateDialog.show();
            }
        });
        //BottomSheet 확인버튼
        DateDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDateTime = selectStartDateTime;
                endDateTime = selectEndDateTime;
                startTextView.setText(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                endTextView.setText(endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                DateDialog.cancel();
            }
        });
        //BottomSheet 취소버튼
        DateDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartDateTime = startDateTime;
                selectEndDateTime = endDateTime;
                DateDialog.cancel();
            }
        });
        //저장버튼
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title =titleTextView.getText().toString();
                if(title==""){
                    title="미지정";
                }
                if (id==-1){
                    ContentValues values = new ContentValues();
                    values.put("startDate", startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    values.put("endDate", endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    values.put("title",title);
                    db.insert("schedule",null,values);
                    finish();
                }
                else{
                    ContentValues values = new ContentValues();
                    values.put("startDate", startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    values.put("endDate", endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    values.put("title",title);
                    db.update("schedule",values,"_id = ?",new String[]{id.toString()});
                    finish();
                }
                Intent intent = new Intent(addScheduleActivity.this, AlarmReceiver.class);
                intent.putExtra("title",title);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(addScheduleActivity.this, 0, intent, 0);
                AlarmManager alarmManager=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                long time = startDateTime.minusMinutes(5).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,time,alarmIntent);
                }
            }
        });
        //취소버튼
        findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((TimePicker)DateDialog.findViewById(R.id.timePicker)).setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if (selectOption == "start"){
                    selectStartDateTime = selectStartDateTime.withHour(i).withMinute(i1);
                }
                else{
                    selectEndDateTime = selectEndDateTime.withHour(i).withMinute(i1);
                }
                setSelectDate();
            }
        });
    }

    private Map<String,String> monthYearFromDate(LocalDateTime date){
        Map<String,String> yearMonth = new HashMap<>();
        yearMonth.put("year",date.format(DateTimeFormatter.ofPattern("yyyy년")));
        yearMonth.put("month",date.format(DateTimeFormatter.ofPattern("MM월")));
        return yearMonth;
    }

    private void setMonthView(){
        LocalDateTime selectDate = selectStartDateTime;
        Map yearMonth = monthYearFromDate(selectDate);
        TextView monthTextView = DateDialog.findViewById(R.id.txt_selectmonth);
        TextView yearTextView = DateDialog.findViewById(R.id.txt_selectyear);
        monthTextView.setText(yearMonth.get("month").toString());
        yearTextView.setText(yearMonth.get("year").toString());
        ArrayList<cell> dayList = dayInMonthArray(selectDate);
        SelectCalendarAdapter adapter = new SelectCalendarAdapter(selectStartDateTime.toLocalDate(), selectEndDateTime.toLocalDate(),selectOption
                ,dayList, addScheduleActivity.this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(),7);
        calendarView.setLayoutManager(manager);
        calendarView.setAdapter(adapter);
    }
    private ArrayList<cell> dayInMonthArray(LocalDateTime date){
        ArrayList<cell> dayList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        YearMonth prevYearMonth = YearMonth.from(date.minusMonths(1));
        YearMonth nextYearMonth = YearMonth.from(date.plusMonths(1));
        int prevMonthLastDay = prevYearMonth.lengthOfMonth();
        int lastDay = yearMonth.lengthOfMonth();
        LocalDateTime firstDay = date.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue();
        int year=0;
        int month=0;
        int day=0;
        boolean activation = false;
        for (int i=1;i<43;i++){
            if (i<=dayOfWeek){
                year = prevYearMonth.getYear();
                month = prevYearMonth.getMonthValue();
                day = prevMonthLastDay-(dayOfWeek-i);
                activation = false;
            }
            else if(i > lastDay + dayOfWeek){
                year = nextYearMonth.getYear();
                month = nextYearMonth.getMonthValue();
                day = i-(lastDay + dayOfWeek);
                activation = false;
            }
            else{
                year = yearMonth.getYear();
                month = yearMonth.getMonthValue();
                day = i-dayOfWeek;
                activation = true;
            }
            dayList.add(new cell(year,month,day,activation));
        }
        return dayList;
    }
    @Override
    public void onItemClick(Schedule schedule){}
    @Override
    public void onItemClick(ArrayList<Integer> reservation) {}
    //일반적인 이벤트
    @Override
    public void onItemClick(LocalDate date) {
        if (selectOption == "start"){
            selectStartDateTime = date.atStartOfDay();
            selectStartDateTime = selectStartDateTime.withHour(timePicker.getHour()).withMinute(timePicker.getMinute());
        }
        else{
            selectEndDateTime = date.atStartOfDay();
            selectEndDateTime = selectEndDateTime.withHour(timePicker.getHour()).withMinute(timePicker.getMinute());
        }
        setSelectDate();
    }
    //시작과 종료 일자 충돌로인한 이벤트
    @Override
    public void onItemClick(LocalDate startDate,LocalDate EndDate) {
        selectStartDateTime = startDate.atStartOfDay();
        selectStartDateTime = selectStartDateTime.withHour(timePicker.getHour()).withMinute(timePicker.getMinute());
        selectEndDateTime = EndDate.atStartOfDay();
        selectEndDateTime = selectEndDateTime.withHour(timePicker.getHour()).withMinute(timePicker.getMinute());
        setSelectDate();
    }

    public void createScheduleAlarm(Context context,LocalDateTime date,String title){

    }
}
