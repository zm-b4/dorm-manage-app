package com.example.dormmanage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryDormActivity extends AppCompatActivity {
    private EditText etQueryContent;
    private NoScrollListView lvResult; // 替换为自定义ListView
    private RadioGroup rgQueryType;
    private DBHelper dbHelper;
    private int queryType = 0; // 0-学号 1-学院 2-班级 3-宿舍号 4-全部
    private List<Map<String, String>> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_dorm);

        // 初始化控件
        etQueryContent = findViewById(R.id.et_query_content);
        lvResult = findViewById(R.id.lv_result); // 自定义ListView
        rgQueryType = findViewById(R.id.rg_query_type);
        dbHelper = new DBHelper(this);

        // 切换查询类型
        rgQueryType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_stu_id) {
                queryType = 0;
                etQueryContent.setHint("请输入学号");
                etQueryContent.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_college) {
                queryType = 1;
                etQueryContent.setHint("请输入学院");
                etQueryContent.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_class) {
                queryType = 2;
                etQueryContent.setHint("请输入班级");
                etQueryContent.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_dorm_num) {
                queryType = 3;
                etQueryContent.setHint("请输入宿舍号");
                etQueryContent.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_all) {
                queryType = 4;
                etQueryContent.setVisibility(View.GONE); // 隐藏输入框
            }
        });

        // 查询按钮
        findViewById(R.id.btn_query).setOnClickListener(v -> queryStuInfo());
        // 清空按钮
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            etQueryContent.setText("");
            studentList.clear();
            lvResult.setAdapter(null);
        });

        // ListView点击跳转详情
        lvResult.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> student = studentList.get(position);
            Intent intent = new Intent(QueryDormActivity.this, QueryDetailActivity.class);
            intent.putExtra("stu_id", student.get("stu_id"));
            intent.putExtra("stu_name", student.get("stu_name"));
            intent.putExtra("building", student.get("building"));
            intent.putExtra("dorm_num", student.get("dorm_num"));
            intent.putExtra("bed_num", student.get("bed_num"));
            intent.putExtra("college", student.get("college"));
            intent.putExtra("class_name", student.get("class_name"));
            intent.putExtra("phone", student.get("phone"));
            intent.putExtra("check_in_time", student.get("check_in_time"));
            startActivity(intent);
        });

        Toast.makeText(this, "查询页面加载成功", Toast.LENGTH_SHORT).show();
    }

    // 多条件+全部查询逻辑
    private void queryStuInfo() {
        studentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // 根据类型查询
        switch (queryType) {
            case 0: // 学号
                String stuId = etQueryContent.getText().toString().trim();
                if (stuId.isEmpty()) {
                    Toast.makeText(this, "请输入学号", Toast.LENGTH_SHORT).show();
                    return;
                }
                cursor = db.query("dorm", null, "stu_id=?", new String[]{stuId}, null, null, null);
                break;
            case 1: // 学院
                String college = etQueryContent.getText().toString().trim();
                if (college.isEmpty()) {
                    Toast.makeText(this, "请输入学院", Toast.LENGTH_SHORT).show();
                    return;
                }
                cursor = db.query("dorm", null, "college LIKE ?", new String[]{"%" + college + "%"}, null, null, null);
                break;
            case 2: // 班级
                String className = etQueryContent.getText().toString().trim();
                if (className.isEmpty()) {
                    Toast.makeText(this, "请输入班级", Toast.LENGTH_SHORT).show();
                    return;
                }
                cursor = db.query("dorm", null, "class_name LIKE ?", new String[]{"%" + className + "%"}, null, null, null);
                break;
            case 3: // 宿舍号
                String dormNum = etQueryContent.getText().toString().trim();
                if (dormNum.isEmpty()) {
                    Toast.makeText(this, "请输入宿舍号", Toast.LENGTH_SHORT).show();
                    return;
                }
                cursor = db.query("dorm", null, "dorm_num LIKE ?", new String[]{"%" + dormNum + "%"}, null, null, null);
                break;
            case 4: // 全部学生
                cursor = db.query("dorm", null, null, null, null, null, "stu_id ASC");
                break;
        }

        // 处理结果
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                List<String> resultList = new ArrayList<>();
                do {
                    Map<String, String> student = new HashMap<>();
                    student.put("stu_id", cursor.getString(cursor.getColumnIndex("stu_id")));
                    student.put("stu_name", cursor.getString(cursor.getColumnIndex("stu_name")));
                    student.put("building", cursor.getString(cursor.getColumnIndex("building")));
                    student.put("dorm_num", cursor.getString(cursor.getColumnIndex("dorm_num")));
                    student.put("bed_num", cursor.getString(cursor.getColumnIndex("bed_num")));
                    student.put("college", cursor.getString(cursor.getColumnIndex("college")));
                    student.put("class_name", cursor.getString(cursor.getColumnIndex("class_name")));
                    student.put("phone", cursor.getString(cursor.getColumnIndex("phone")));
                    student.put("check_in_time", cursor.getString(cursor.getColumnIndex("check_in_time")));
                    studentList.add(student);
                    resultList.add("姓名：" + student.get("stu_name") + " | 学号：" + student.get("stu_id"));
                } while (cursor.moveToNext());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, resultList);
                lvResult.setAdapter(adapter);
                Toast.makeText(this, "共查询到" + studentList.size() + "条结果", Toast.LENGTH_SHORT).show();
            } else {
                lvResult.setAdapter(null);
                Toast.makeText(this, "无匹配结果", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
        db.close();
    }
}