package com.dilip.cloudattendance.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "CloudAttendance";
    private static final String TABLE_LOGIN = "login";
    private static final String KEY_ID = "id";
    private static final String KEY_id_no = "id_no";
    private static final String KEY_URN = "urn_no";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST = "surname";
    private static final String KEY_COURSE = "course";
    private static final String KEY_YEAR = "year";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_Role = "role";
    private static final String KEY_VERIFY = "verify";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_id_no + " TEXT,"
            + KEY_URN + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_LAST + " TEXT,"
            + KEY_COURSE + " TEXT,"
            + KEY_YEAR + " TEXT,"
            + KEY_SEMESTER + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_Role + " TEXT,"
            + KEY_VERIFY + " TEXT" + ")";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        onCreate(db);
    }

    public void addUser(String uid, String id_no ,String urn_no, String name, String last, String course,String year, String semester, String phone, String email, String role,String verify) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("name",email);
        ContentValues values = new ContentValues();
        values.put(KEY_id_no, id_no); // id
        values.put(KEY_URN, urn_no); // urn
        values.put(KEY_NAME, name); // FIRSTNAME
        values.put(KEY_LAST, last); // LASTNAME
        values.put(KEY_COURSE, course); // COURSE
        values.put(KEY_YEAR, year); // YEAR
        values.put(KEY_SEMESTER, semester); // semester
        values.put(KEY_PHONE, phone); // PHONE
        values.put(KEY_EMAIL, email); // EMAIL
        values.put(KEY_Role, role); // role
        values.put(KEY_VERIFY, verify); // verify status

        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("id_no", cursor.getString(1));
            user.put("urn_no", cursor.getString(2));
            user.put("name", cursor.getString(3));
            user.put("surname", cursor.getString(4));
            user.put("course", cursor.getString(5));
            user.put("year", cursor.getString(6));
            user.put("semester", cursor.getString(7));
            user.put("phone", cursor.getString(8));
            user.put("email", cursor.getString(9));
            user.put("role", cursor.getString(10));
            user.put("verify", cursor.getString(11));
        }
        cursor.close();
        db.close();

        return user;
    }
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }

}
