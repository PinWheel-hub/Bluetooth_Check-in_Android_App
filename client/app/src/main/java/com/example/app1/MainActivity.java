package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtnbtn1;
    private Button mBtnbtn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnbtn1 = (Button) findViewById(R.id.btn_teacher);
        mBtnbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到教师注册登录界面
                Intent intent = new Intent(MainActivity.this,Logint.class);
                startActivity(intent);
            }
        });
        mBtnbtn2 = (Button) findViewById(R.id.btn_student);
        mBtnbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到学生注册登录界面
                Intent intent = new Intent(MainActivity.this,Logins.class);
                startActivity(intent);
            }
        });
    }
}