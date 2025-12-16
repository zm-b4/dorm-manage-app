package com.example.dormmanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定功能按钮
        LinearLayout llAdd = findViewById(R.id.ll_add);
        LinearLayout llEdit = findViewById(R.id.ll_edit);
        LinearLayout llQuery = findViewById(R.id.ll_query);
        LinearLayout llDelete = findViewById(R.id.ll_delete);
        // 绑定退出登录按钮
        TextView tvLogout = findViewById(R.id.tv_logout);

        // 新增学生
        llAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddDormActivity.class);
            startActivity(intent);
        });

        // 修改学生
        llEdit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditDormActivity.class);
            startActivity(intent);
        });

        // 查询学生
        llQuery.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QueryDormActivity.class);
            startActivity(intent);
        });

        // 删除学生
        llDelete.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeleteDormActivity.class);
            startActivity(intent);
        });

        // 退出登录功能
        tvLogout.setOnClickListener(v -> {
            // 弹出确认对话框
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("确认退出")
                    .setMessage("是否确定退出登录？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 跳转到登录页，关闭当前页面
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
}