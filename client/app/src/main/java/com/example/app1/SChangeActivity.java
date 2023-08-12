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

public class SChangeActivity extends AppCompatActivity {

    private Button titleBack;
    private TextView titleText;
    private EditText password;
    private EditText number;
    private EditText name;
    private EditText college;

    private String current_Spwd;
    private String current_Snum;
    private String current_Sname;
    private String current_Sschool;
    private String msg_code;
    private String msg_code1;
    private MyApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schange);
        SendMessage_currentinformation();
        password = findViewById(R.id.et_scpassword);
        number = findViewById(R.id.et_scnumber);
        name = findViewById(R.id.et_scname);
        college = findViewById(R.id.et_sccollege);
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("修改个人信息");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师主页
                Intent intent = new Intent(SChangeActivity.this,StudentHomeActivity.class);
                startActivity(intent);
            }
        });

        titleBack=(Button) findViewById(R.id.btn_scfinish);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Spwd = String.valueOf(password.getText());
                String Snum = String.valueOf(number.getText());
                String Sname = String.valueOf(name.getText());
                String Sschool = String.valueOf(college.getText());
                app=(MyApplication) getApplication();
                String url = app.gethost()+"/Student/alterinformation/";//替换成自己的服务器地址
                SendMessage(url, Spwd, Snum, Sname, Sschool);
            }
        });
    }

    private void SendMessage(String url, final String pwd, String num, String name, String school) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("new_Spwd", pwd);
        formBuilder.add("new_Snum", num);
        formBuilder.add("new_Sname", name);
        formBuilder.add("new_Sschool", school);
        app = (MyApplication) getApplication();
        Request request = new Request.Builder().url(url)
                .addHeader("Cookie", "session=" + app.getsessionid())
                .post(formBuilder.build()).build();
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
                                Toast.makeText(SChangeActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(SChangeActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SChangeActivity.this, "修改失败，内容不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SChangeActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SChangeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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

    private void SendMessage_currentinformation() {
        OkHttpClient client = new OkHttpClient();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/student_restful/";
        Request request = new Request.Builder().url(url)
                .addHeader("Cookie", "session=" + app.getsessionid()).get().build();
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
                                Toast.makeText(SChangeActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                parseJsonWithJsonObject1(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (msg_code1) {
                            case "1":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        titleText=(EditText) findViewById(R.id.et_scpassword);
                                        titleText.setText(current_Spwd);
                                        titleText=(EditText) findViewById(R.id.et_scname);
                                        titleText.setText(current_Sname);
                                        titleText=(EditText) findViewById(R.id.et_scnumber);
                                        titleText.setText(current_Snum);
                                        titleText=(EditText) findViewById(R.id.et_sccollege);
                                        titleText.setText(current_Sschool);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SChangeActivity.this, "用户查询错误", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SChangeActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SChangeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });

    }
    private void parseJsonWithJsonObject1(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code1=jsonObject.getString("msg_code");
            current_Spwd=jsonObject.getString("Spwd");
            current_Snum=jsonObject.getString("Snum");
            current_Sname=jsonObject.getString("Sname");
            current_Sschool=jsonObject.getString("Sschool");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}