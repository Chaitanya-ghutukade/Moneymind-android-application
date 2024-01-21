package com.example.moneymind;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {
private  static final String DATABASE_NAME="MainDB";
private static final int DATABASE_VERSION=2;
private  static final String TABLE_REGISTER="register";
    private  static final String KEY_FullNAME="fullName";
    private  static final String KEY_EMAIL="Email";
    private  static final String KEY_USERNAME="Username";
    private  static final String KEY_PASSWORD="Password";

    public MyDBHelper( Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_REGISTER + "("+KEY_FullNAME+" TEXT,"+KEY_EMAIL+" TEXT,"+
                KEY_USERNAME+" TEXT PRIMARY KEY,"+KEY_PASSWORD+" TEXT NOT NULL"+")");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_REGISTER);
        onCreate(db);

    }
    public void register_user(String name,String email,String username,String password){
        SQLiteDatabase db1=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_FullNAME,name);
        values.put(KEY_EMAIL,email);
        values.put(KEY_USERNAME,username);
        values.put(KEY_PASSWORD,password);
        db1.insert(TABLE_REGISTER,null,values);
    }
}