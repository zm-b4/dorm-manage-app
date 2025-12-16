package com.example.dormmanage;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dorm.db";
    private static final int DB_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS dorm (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "stu_id TEXT UNIQUE," +
                "stu_name TEXT," +
                "gender TEXT," +
                "building TEXT," +
                "dorm_num TEXT," +
                "bed_num TEXT," +
                "college TEXT," +
                "class_name TEXT," +
                "phone TEXT," +
                "check_in_time TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dorm");
        onCreate(db);
    }

    // 校验学号是否存在
    public boolean isStuIdExists(String stuId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"stu_id"};
        String selection = "stu_id = ?";
        String[] selectionArgs = {stuId};

        Cursor cursor = db.query("dorm", columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // 插入学生信息
    public boolean insertStuInfo(String stuId, String stuName, String gender,
                                 String building, String dormNum, String bedNum,
                                 String college, String className, String phone, String checkIn) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("stu_id", stuId);
            values.put("stu_name", stuName);
            values.put("gender", gender);
            values.put("building", building);
            values.put("dorm_num", dormNum);
            values.put("bed_num", bedNum);
            values.put("college", college);
            values.put("class_name", className);
            values.put("phone", phone);
            values.put("check_in_time", checkIn);

            long result = db.insert("dorm", null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}