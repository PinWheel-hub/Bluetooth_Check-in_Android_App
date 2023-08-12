package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Logins extends AppCompatActivity {

    private Button mBtnRegisterS;
    private Button mBtnLoginS;
    private Button titleBack;
    private TextView titleText;
    private EditText username;
    private EditText password;
    private String msg_code;
    private MyApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logins);
        username = findViewById(R.id.et_1);
        password = findViewById(R.id.et_2);
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("学生登录");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到初始界面
                Intent intent = new Intent(Logins.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mBtnRegisterS = (Button) findViewById(R.id.btn_registers);
        mBtnRegisterS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到学生注册界面
                Intent intent = new Intent(Logins.this,Registers.class);
                startActivity(intent);
            }
        });
        mBtnLoginS = (Button) findViewById(R.id.btn_logins);
        mBtnLoginS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Stel = String.valueOf(username.getText());
                String Spwd = String.valueOf(password.getText());
                app=(MyApplication) getApplication();
                String url = app.gethost()+"/Student/login/";//替换成自己的服务器地址
                SendMessage(url, Stel, Spwd);
            }
        });
    }

    private void SendMessage(String url, final String tel, String pwd) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Stel", tel);
        formBuilder.add("Spwd", pwd);
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
                                Toast.makeText(Logins.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Headers headers = response.headers();
                                        List<String> cookies = headers.values("Set-Cookie");
                                        String session = cookies.get(0);
                                        String sessionID = session.substring(0, session.indexOf(";"));
                                        sessionID = sessionID.substring(8);
                                        Log.i("info_s", "session is  :" + sessionID);
                                        app = (MyApplication) getApplication();
                                        app.setsessionid(sessionID);
                                        Toast.makeText(Logins.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        //跳转到学生主页
                                        Intent intent = new Intent(Logins.this,StudentHomeActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Logins.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Logins.this, "用户未注册", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Logins.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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