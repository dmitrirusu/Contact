package com.example.sylar.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    static final String PHOTO_ID = "photoID";
    static final String NAME = "name";
    static final String NUMBER = "number";


    DBHelper(Context context) {
        super(context, "mydb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mytable(id integer primary key autoincrement, "+PHOTO_ID + " text, "
                + NAME + " text, " +NUMBER + " text);" );
        db.execSQL("delete from "+ "mytable");

        ContentValues cv = new ContentValues();
        cv.put(PHOTO_ID,"");
        cv.put(NAME,"Вася");
        cv.put(NUMBER,"02166542");
        db.insert("mytable", null, cv);

        cv.put(PHOTO_ID,"");
        cv.put(NAME,"Дима");
        cv.put(NUMBER,"069851284");
        db.insert("mytable", null, cv);

        cv.put(PHOTO_ID,"");
        cv.put(NAME,"Валик");
        cv.put(NUMBER,"0321564");
        db.insert("mytable", null, cv);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}