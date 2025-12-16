package com.example.dormmanage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddDormActivity extends AppCompatActivity {
    private EditText etStuId, etStuName, etDormNum, etClass, etPhone, etCheckIn;
    private Spinner spGender, spCampus, spBuildingNum, spBedNum, spCollege;
    private DBHelper dbHelper;

    // 园区列表
    private String[] campusList = {"桃园", "李园", "桂园"};
    // 各园区对应的楼栋号
    private String[][] buildingNumList = {
            {"一", "二", "三", "四"},       // 桃园：1-4
            {"一", "二", "三", "四", "五"}, // 李园：1-5
            {"一", "二", "三", "四"}        // 桂园：1-4
    };
    // 学院列表
    private String[] collegeList;

    // 选中的园区和楼栋
    private String selectedCampus = "";
    private String selectedBuildingNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dorm);

        // 初始化控件
        etStuId = findViewById(R.id.et_stu_id);
        etStuName = findViewById(R.id.et_stu_name);
        etDormNum = findViewById(R.id.et_dorm_num);
        etClass = findViewById(R.id.et_class);
        etPhone = findViewById(R.id.et_phone);
        etCheckIn = findViewById(R.id.et_check_in);
        spGender = findViewById(R.id.sp_gender);
        spCampus = findViewById(R.id.sp_campus);
        spBuildingNum = findViewById(R.id.sp_building_num);
        spBedNum = findViewById(R.id.sp_bed_num);
        spCollege = findViewById(R.id.sp_college);

        // 初始化数据库
        dbHelper = new DBHelper(this);

        // 获取学院列表
        collegeList = getResources().getStringArray(R.array.college_list);

        // 1. 初始化性别Spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_list, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        // 2. 初始化园区Spinner
        ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(
                this, R.array.campus_list, android.R.layout.simple_spinner_item);
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCampus.setAdapter(campusAdapter);

        // 3. 园区选择监听（联动更新楼栋号）
        spCampus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCampus = campusList[position];
                // 根据选中的园区，更新楼栋号列表
                ArrayAdapter<String> buildingNumAdapter = new ArrayAdapter<>(
                        AddDormActivity.this,
                        android.R.layout.simple_spinner_item,
                        buildingNumList[position]);
                buildingNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spBuildingNum.setAdapter(buildingNumAdapter);
                // 重置楼栋号选择
                selectedBuildingNum = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCampus = "";
            }
        });

        // 4. 楼栋号选择监听
        spBuildingNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBuildingNum = buildingNumList[spCampus.getSelectedItemPosition()][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBuildingNum = "";
            }
        });

        // 5. 初始化床位号Spinner
        ArrayAdapter<CharSequence> bedNumAdapter = ArrayAdapter.createFromResource(
                this, R.array.bed_num_list, android.R.layout.simple_spinner_item);
        bedNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBedNum.setAdapter(bedNumAdapter);

        // 6. 初始化学院Spinner
        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, collegeList);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCollege.setAdapter(collegeAdapter);

        // 7. 学号唯一性实时校验
        etStuId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String stuId = s.toString().trim();
                if (!TextUtils.isEmpty(stuId)) {
                    if (dbHelper.isStuIdExists(stuId)) {
                        etStuId.setError("该学号已存在！",
                                ContextCompat.getDrawable(AddDormActivity.this, android.R.drawable.ic_dialog_alert));
                    } else {
                        etStuId.setError(null);
                    }
                }
            }
        });

        // 8. 手机号格式校验
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                if (!TextUtils.isEmpty(phone) && phone.length() != 11) {
                    etPhone.setError("手机号必须为11位！",
                            ContextCompat.getDrawable(AddDormActivity.this, android.R.drawable.ic_dialog_alert));
                } else {
                    etPhone.setError(null);
                }
            }
        });

        // 9. 高级日历选择器（支持19xx年）
        etCheckIn.setOnClickListener(v -> showAdvancedDatePicker());

        // 10. 提交按钮点击事件
        findViewById(R.id.btn_submit).setOnClickListener(v -> addStuInfo());
    }

    // 显示高级日历选择器（支持选择19xx年）
    private void showAdvancedDatePicker() {
        // 构建日历约束：起始时间设为1900年1月1日，结束时间设为当前时间+1年
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // 设置起始时间（1900年1月1日）
        Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startCal.set(1900, Calendar.JANUARY, 1);
        long startMillis = startCal.getTimeInMillis();

        // 设置结束时间（当前时间+1年）
        Calendar endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endCal.add(Calendar.YEAR, 1);
        long endMillis = endCal.getTimeInMillis();

        // 应用时间范围（移除只能选未来日期的限制）
        constraintsBuilder.setStart(startMillis);
        constraintsBuilder.setEnd(endMillis);

        // 构建日期选择器（支持选择过去/未来任意日期，包括19xx年）
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("选择入住日期")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // 默认选中今天
                .build();

        // 日期选择监听
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // 格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String selectedDate = sdf.format(new Date(selection));
            etCheckIn.setText(selectedDate);
        });

        // 显示日期选择器
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    // 信息验证+新增逻辑
    private void addStuInfo() {
        // 获取输入内容
        String stuId = etStuId.getText().toString().trim();
        String stuName = etStuName.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();
        String fullBuilding = selectedCampus.substring(0, 1) + selectedBuildingNum;
        String dormNum = etDormNum.getText().toString().trim();
        String bedNum = spBedNum.getSelectedItem().toString();
        String college = spCollege.getSelectedItem().toString();
        String className = etClass.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String checkIn = etCheckIn.getText().toString().trim();

        // 1. 非空校验
        if (TextUtils.isEmpty(stuId)) {
            showError(etStuId, "学号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(stuName)) {
            showError(etStuName, "姓名不能为空！");
            return;
        }
        if (TextUtils.isEmpty(selectedCampus) || TextUtils.isEmpty(selectedBuildingNum)) {
            Toast.makeText(this, "请选择园区和楼栋号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(dormNum)) {
            showError(etDormNum, "宿舍号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(checkIn)) {
            showError(etCheckIn, "入住时间不能为空！");
            return;
        }
        if (TextUtils.isEmpty(className)) {
            showError(etClass, "班级不能为空！");
            return;
        }

        // 2. 学号唯一性校验
        if (dbHelper.isStuIdExists(stuId)) {
            showError(etStuId, "该学号已存在，无法重复添加！");
            return;
        }

        // 3. 手机号格式校验（非必填，但填了就要符合格式）
        if (!TextUtils.isEmpty(phone) && phone.length() != 11) {
            showError(etPhone, "手机号必须为11位！");
            return;
        }

        // 4. 学号格式校验（可选，示例：8位数字）
        if (!stuId.matches("^\\d{9}$")) {
            showError(etStuId, "学号格式错误（请输入9位数字）！");
            return;
        }

        // 5. 插入数据库
        try {
            boolean isSuccess = dbHelper.insertStuInfo(
                    stuId, stuName, gender, fullBuilding, dormNum, bedNum,
                    college, className, phone, checkIn);

            if (isSuccess) {
                Toast.makeText(this, "新增成功！", Toast.LENGTH_SHORT).show();
                clearInput(); // 清空输入框
            } else {
                Toast.makeText(this, "新增失败，请重试！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "新增失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 显示错误提示
    private void showError(EditText editText, String message) {
        editText.setError(message, ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert));
        editText.requestFocus();
    }

    // 清空输入框
    private void clearInput() {
        etStuId.setText("");
        etStuName.setText("");
        etDormNum.setText("");
        etClass.setText("");
        etPhone.setText("");
        etCheckIn.setText("");
        spGender.setSelection(0);
        spCampus.setSelection(0);
        spBuildingNum.setSelection(0);
        spBedNum.setSelection(0);
        spCollege.setSelection(0);
        selectedCampus = "";
        selectedBuildingNum = "";

        // 清除错误提示
        etStuId.setError(null);
        etStuName.setError(null);
        etDormNum.setError(null);
        etPhone.setError(null);
        etCheckIn.setError(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}