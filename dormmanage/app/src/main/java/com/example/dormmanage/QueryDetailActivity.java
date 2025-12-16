package com.example.dormmanage;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QueryDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_detail);

        // 获取查询页面传递的学生信息
        String stuId = getIntent().getStringExtra("stu_id");
        String stuName = getIntent().getStringExtra("stu_name");
        String building = getIntent().getStringExtra("building");
        String dormNum = getIntent().getStringExtra("dorm_num");
        String bedNum = getIntent().getStringExtra("bed_num");
        String college = getIntent().getStringExtra("college");
        String className = getIntent().getStringExtra("class_name");
        String phone = getIntent().getStringExtra("phone");
        String checkIn = getIntent().getStringExtra("check_in_time");

        // 初始化详情页面控件
        TextView tvStuId = findViewById(R.id.tv_detail_stu_id);
        TextView tvStuName = findViewById(R.id.tv_detail_stu_name);
        TextView tvBuilding = findViewById(R.id.tv_detail_building);
        TextView tvDormNum = findViewById(R.id.tv_detail_dorm_num);
        TextView tvBedNum = findViewById(R.id.tv_detail_bed_num);
        TextView tvCollege = findViewById(R.id.tv_detail_college);
        TextView tvClass = findViewById(R.id.tv_detail_class);
        TextView tvPhone = findViewById(R.id.tv_detail_phone);
        TextView tvCheckIn = findViewById(R.id.tv_detail_check_in);

        // 设置详情信息
        tvStuId.setText("学号：" + stuId);
        tvStuName.setText("姓名：" + stuName);
        tvBuilding.setText("楼栋：" + building);
        tvDormNum.setText("宿舍号：" + dormNum);
        tvBedNum.setText("床位号：" + bedNum);
        tvCollege.setText("学院：" + college);
        tvClass.setText("班级：" + className);
        tvPhone.setText("电话：" + (phone.isEmpty() ? "未填写" : phone));
        tvCheckIn.setText("入住时间：" + (checkIn.isEmpty() ? "未填写" : checkIn));
    }
}