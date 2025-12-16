package com.example.dormmanage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditDormActivity extends AppCompatActivity {
    private EditText etQueryStuId, etBuilding, etDormNum, etBedNum, etStuName, etCollege, etClass, etPhone, etCheckIn;
    private DBHelper dbHelper;
    private String currentStuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 核心：先加载布局
        setContentView(R.layout.activity_edit_dorm);

        // 初始化所有控件
        etQueryStuId = findViewById(R.id.et_query_stu_id);
        etBuilding = findViewById(R.id.et_building);
        etDormNum = findViewById(R.id.et_dorm_num);
        etBedNum = findViewById(R.id.et_bed_num);
        etStuName = findViewById(R.id.et_stu_name);
        etCollege = findViewById(R.id.et_college);
        etClass = findViewById(R.id.et_class);
        etPhone = findViewById(R.id.et_phone);
        etCheckIn = findViewById(R.id.et_check_in);
        dbHelper = new DBHelper(this);

        // 绑定按钮
        findViewById(R.id.btn_query).setOnClickListener(v -> queryStuInfo());
        findViewById(R.id.btn_save).setOnClickListener(v -> saveEditInfo());

        Toast.makeText(this, "修改页面加载成功", Toast.LENGTH_SHORT).show();
    }

    private void queryStuInfo() {
        currentStuId = etQueryStuId.getText().toString().trim();
        if (currentStuId.isEmpty()) {
            Toast.makeText(this, "请输入要修改的学号", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("dorm", null, "stu_id=?", new String[]{currentStuId}, null, null, null);

        if (cursor.moveToFirst()) {
            etBuilding.setText(cursor.getString(cursor.getColumnIndex("building")));
            etDormNum.setText(cursor.getString(cursor.getColumnIndex("dorm_num")));
            etBedNum.setText(cursor.getString(cursor.getColumnIndex("bed_num")));
            etStuName.setText(cursor.getString(cursor.getColumnIndex("stu_name")));
            etCollege.setText(cursor.getString(cursor.getColumnIndex("college")));
            etClass.setText(cursor.getString(cursor.getColumnIndex("class_name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndex("phone")));
            etCheckIn.setText(cursor.getString(cursor.getColumnIndex("check_in_time")));
            Toast.makeText(this, "加载信息成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "未找到该学号", Toast.LENGTH_SHORT).show();
            clearEdit();
        }
        cursor.close();
        db.close();
    }

    private void saveEditInfo() {
        if (currentStuId == null || currentStuId.isEmpty()) {
            Toast.makeText(this, "请先查询学生", Toast.LENGTH_SHORT).show();
            return;
        }

        String building = etBuilding.getText().toString().trim();
        String dormNum = etDormNum.getText().toString().trim();
        String bedNum = etBedNum.getText().toString().trim();
        String college = etCollege.getText().toString().trim();
        String className = etClass.getText().toString().trim();

        if (building.isEmpty() || dormNum.isEmpty() || bedNum.isEmpty() || college.isEmpty() || className.isEmpty()) {
            Toast.makeText(this, "必填项不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("building", building);
        values.put("dorm_num", dormNum);
        values.put("bed_num", bedNum);
        values.put("college", college);
        values.put("class_name", className);
        values.put("phone", etPhone.getText().toString().trim());
        values.put("check_in_time", etCheckIn.getText().toString().trim());

        int rows = db.update("dorm", values, "stu_id=?", new String[]{currentStuId});
        if (rows > 0) {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            clearEdit();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void clearEdit() {
        etQueryStuId.setText("");
        etBuilding.setText("");
        etDormNum.setText("");
        etBedNum.setText("");
        etStuName.setText("");
        etCollege.setText("");
        etClass.setText("");
        etPhone.setText("");
        etCheckIn.setText("");
        currentStuId = null;
    }
}