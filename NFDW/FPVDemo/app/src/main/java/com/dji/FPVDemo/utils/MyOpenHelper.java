package com.dji.FPVDemo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by xiaoT on 2017/2/5.
 */

public class MyOpenHelper extends SQLiteOpenHelper {
    private MyOpenHelper myOpenHelper;

    public MyOpenHelper(Context context ) {
        super(context, "mydata.db",null, 1);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table info(_id integer" +
                " primary key autoincrement,"
                + "time varchar(5000000),dist1 varchar(5000000)," +
                "dist2 varchar(5000000),place varchar(5000000), sign varchar(5000000))");
        db.execSQL("create table catalog(_id integer" +
                " primary key autoincrement,"
                + "time varchar(10000),dist1 varchar(10000)," +
                "dist2 varchar(10000),place varchar(10000),sign varchar(10000), shortest_dis varchar(10000))");
        ContentValues values_temp = new ContentValues();
        values_temp.put("time", "null");
        values_temp.put("dist1", "null");
        values_temp.put("dist2", "null");
        values_temp.put("place", "null");
        values_temp.put("sign", "0");
        db.insert("info", null, values_temp);

        ContentValues values_temp_2 = new ContentValues();
        values_temp_2.put("time", "null");
        values_temp_2.put("dist1", "null");
        values_temp_2.put("dist2", "null");
        values_temp_2.put("place", "null");
        values_temp_2.put("sign", "0");
        values_temp_2.put("shortest_dis", "null");
        db.insert("catalog", null, values_temp_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
