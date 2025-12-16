package com.example.dormmanage;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DBHelper dbHelper;
    // 新增：密码切换图标
    private ImageView ivPwdToggle;
    private boolean isPwdVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化原有控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        // 绑定密码切换按钮
        ivPwdToggle = findViewById(R.id.iv_pwd_toggle);

        dbHelper = new DBHelper(this);

        // 原有登录逻辑
        btnLogin.setOnClickListener(v -> login());

        // 新增：密码显示/隐藏切换（只用系统图标，无需额外资源）
        ivPwdToggle.setOnClickListener(v -> {
            if (isPwdVisible) {
                // 隐藏密码
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivPwdToggle.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                // 显示密码
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivPwdToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            // 光标保持在密码末尾
            etPassword.setSelection(etPassword.getText().length());
            isPwdVisible = !isPwdVisible;
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名/密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证账号（默认admin/123456）
        if (username.equals("admin") && password.equals("123456")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}