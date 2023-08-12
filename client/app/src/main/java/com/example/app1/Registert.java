package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Registert extends AppCompatActivity {

    private Button mBtnFinishrt;
    private Button titleBack;
    private TextView titleText;
    private EditText username;
    private EditText password;
    private EditText number;
    private EditText name;
    private EditText college;
    private String msg_code;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registert);
        username = findViewById(R.id.et_rtusername);
        password = findViewById(R.id.et_rtpassword);
        number = findViewById(R.id.et_rtnumber);
        name = findViewById(R.id.et_rtname);
        college = findViewById(R.id.et_rtcollege);
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("教师注册");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师登录界面
                Intent intent = new Intent(Registert.this,Logint.class);
                startActivity(intent);
            }
        });

        mBtnFinishrt = (Button) findViewById(R.id.btn_rtfinish);
        mBtnFinishrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Ttel = String.valueOf(username.getText());
                String Tpwd = String.valueOf(password.getText());
                String Tnum = String.valueOf(number.getText());
                String Tname = String.valueOf(name.getText());
                String Tschool = String.valueOf(college.getText());
                app = (MyApplication) getApplication();
                String url = app.gethost()+"/Teacher/register/";//替换成自己的服务器地址
                SendMessage(url, Ttel, Tpwd, Tnum, Tname, Tschool);
            }
        });
    }

    private void SendMessage(String url, final String tel, String pwd, String num, String name, String school) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Ttel", tel);
        formBuilder.add("Tpwd", pwd);
        formBuilder.add("Tnum", num);
        formBuilder.add("Tname", name);
        formBuilder.add("Tschool", school);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Registert.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                parseJsonWithJsonObject(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (msg_code) {
                            case "1":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Registert.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        //跳转到学生注册登录界面
                                        Intent intent = new Intent(Registert.this, Logint.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Registert.this, "手机号不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Registert.this, "该手机号已注册", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Registert.this, "该教师已注册", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "5":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Registert.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Registert.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });

    }
    private void parseJsonWithJsonObject(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code=jsonObject.getString("msg_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
