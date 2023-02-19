package com.example.termproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class amendScheduleActivity extends AppCompatActivity {
    TextView titleTextView;
    TextView startDateTimeView;
    TextView endDateTimeView;
    DBHelper helper;
    SQLiteDatabase db;
    Integer id;
    String title;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amendschedule);
        helper = DBHelper.getInstance(this);
        try{
            db=helper.getWritableDatabase();
        }catch (SQLiteException ex) {
            db = helper.getReadableDatabase();
        }

        id = (Integer)getIntent().getExtras().get("id");
        title = (String)getIntent().getExtras().get("title");
        startDateTime = (LocalDateTime) getIntent().getExtras().get("startDateTime");
        endDateTime = (LocalDateTime) getIntent().getExtras().get("endDateTime");

        titleTextView = findViewById(R.id.amend_txt_title);
        startDateTimeView = findViewById(R.id.amend_txt_startDateTime);
        endDateTimeView = findViewById(R.id.amend_txt_endDateTime);

        titleTextView.setText(title);
        startDateTimeView.setText(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        endDateTimeView.setText(endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        findViewById(R.id.amend_btn_backspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.amend_btn_amend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(amendScheduleActivity.this,addScheduleActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("startDateTime", startDateTime);
                intent.putExtra("endDateTime", endDateTime);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.amend_btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.execSQL("DELETE FROM schedule WHERE _id == "+id);
                finish();
            }
        });
    }
}
