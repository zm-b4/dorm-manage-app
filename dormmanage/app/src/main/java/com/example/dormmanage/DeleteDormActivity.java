package com.example.dormmanage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class DeleteDormActivity extends AppCompatActivity {
    private EditText etQueryContent;
    private LinearLayout llStudentList;
    private DBHelper dbHelper;
    private List<String> selectedStuIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_dorm);

        // 初始化控件
        etQueryContent = findViewById(R.id.et_query_content);
        llStudentList = findViewById(R.id.ll_student_list);
        dbHelper = new DBHelper(this);

        // 查询按钮
        findViewById(R.id.btn_query).setOnClickListener(v -> query());
        // 确认删除
        findViewById(R.id.btn_confirm_delete).setOnClickListener(v -> delete());
        // 取消
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    // 兼容完整字段的查询逻辑
    private void query() {
        String content = etQueryContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入学号/宿舍号", Toast.LENGTH_SHORT).show();
            return;
        }

        // 清空旧数据
        selectedStuIds.clear();
        llStudentList.removeAllViews();
        findViewById(R.id.ll_result).setVisibility(View.GONE);
        findViewById(R.id.btn_confirm_delete).setVisibility(View.GONE);

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            // 查询所有字段，避免索引错误
            cursor = db.query(
                    "dorm",
                    null, // 查所有字段
                    "stu_id LIKE ? OR dorm_num LIKE ?",
                    new String[]{"%" + content + "%", "%" + content + "%"},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 按字段名获取（最安全）
                    String stuId = cursor.getString(cursor.getColumnIndex("stu_id"));
                    String stuName = cursor.getString(cursor.getColumnIndex("stu_name"));
                    String building = cursor.getString(cursor.getColumnIndex("building"));
                    String dormNum = cursor.getString(cursor.getColumnIndex("dorm_num"));

                    // 空值替换
                    stuName = (stuName == null) ? "未命名" : stuName;
                    building = (building == null) ? "未知楼栋" : building;
                    dormNum = (dormNum == null) ? "未填写" : dormNum;

                    // 添加卡片（显示姓名+学号+楼栋+宿舍）
                    addCard(stuId, stuName, building, dormNum);

                } while (cursor.moveToNext());

                // 显示结果
                findViewById(R.id.ll_result).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_confirm_delete).setVisibility(View.VISIBLE);
                Toast.makeText(this, "找到" + cursor.getCount() + "条记录", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "未找到匹配记录", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "查询异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // 关闭游标和数据库
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    // 添加带楼栋的学生卡片
    private void addCard(String stuId, String stuName, String building, String dormNum) {
        LinearLayout card = new LinearLayout(this); // 去掉context:
        card.setPadding(16, 16, 16, 16); // 去掉命名参数
        card.setBackgroundResource(R.drawable.shape_card); // 修复资源引用格式
        card.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 12);
        card.setLayoutParams(params);

        // 选择框
        CheckBox cb = new CheckBox(this);
        cb.setPadding(0, 0, 16, 0);
        cb.setButtonTintList(ContextCompat.getColorStateList(this, R.color.primary));
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedStuIds.contains(stuId)) {
                    selectedStuIds.add(stuId);
                }
            } else {
                selectedStuIds.remove(stuId);
            }
        });
        card.addView(cb);

        // 学生信息容器
        LinearLayout infoContainer = new LinearLayout(this);
        infoContainer.setOrientation(LinearLayout.VERTICAL);
        infoContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // 姓名+学号
        TextView tvName = new TextView(this);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        tvName.setText(stuName + "（" + stuId + "）");
        infoContainer.addView(tvName);

        // 楼栋+宿舍
        TextView tvDorm = new TextView(this);
        tvDorm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvDorm.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
        tvDorm.setText("楼栋：" + building + " | 宿舍：" + dormNum);
        tvDorm.setPadding(0, 4, 0, 0);
        infoContainer.addView(tvDorm);

        card.addView(infoContainer);
        llStudentList.addView(card);
    }
    // 安全删除逻辑
    private void delete() {
        if (selectedStuIds.isEmpty()) {
            Toast.makeText(this, "请选择要删除的学生", Toast.LENGTH_SHORT).show();
            return;
        }

        // 二次确认
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定删除选中的" + selectedStuIds.size() + "条记录？\n此操作不可恢复！")
                .setPositiveButton("确定", (dialog, which) -> {
                    SQLiteDatabase db = null;
                    try {
                        db = dbHelper.getWritableDatabase();
                        int deleteCount = 0;
                        // 批量删除
                        for (String stuId : selectedStuIds) {
                            int result = db.delete("dorm", "stu_id=?", new String[]{stuId});
                            if (result > 0) deleteCount++;
                        }
                        // 反馈结果
                        Toast.makeText(this, "成功删除" + deleteCount + "条记录", Toast.LENGTH_SHORT).show();
                        // 重置页面
                        etQueryContent.setText("");
                        selectedStuIds.clear();
                        llStudentList.removeAllViews();
                        findViewById(R.id.ll_result).setVisibility(View.GONE);
                        findViewById(R.id.btn_confirm_delete).setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(this, "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        if (db != null && db.isOpen()) db.close();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}