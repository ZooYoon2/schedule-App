package com.example.termproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper sInstance;
    public DBHelper(@Nullable Context context) {
        super(context, "whatToDo", null, 2);
    }

    public static synchronized DBHelper getInstance(Context context){
        if(sInstance==null){
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE schedule ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " startdate TEXT, enddate TEXT, title TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE todo ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "deadline TEXT, title TEXT, checktodo INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS schedule");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(sqLiteDatabase);
    }
}
