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

public class CreateClassActivity extends AppCompatActivity {

    private Button titleBack;
    private TextView titleText;
    private EditText num;
    private EditText name;
    private EditText term;
    private EditText bluetooth;
    private String msg_code;
    private String Cid;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        name = findViewById(R.id.class_name);
        num = findViewById(R.id.class_num);
        term = findViewById(R.id.class_term);
        bluetooth = findViewById(R.id.class_bluetooth);
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("创建课程");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师主页
                Intent intent = new Intent(CreateClassActivity.this,TeacherHomeActivity.class);
                startActivity(intent);
            }
        });

        titleBack=(Button) findViewById(R.id.btn_ccfinish);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Cname = String.valueOf(name.getText());
                String Cnum = String.valueOf(num.getText());
                String Cterm = String.valueOf(term.getText());
                String Bluetooth = String.valueOf(bluetooth.getText());
                app=(MyApplication) getApplication();
                String url = app.gethost()+"/teacher_curriculum/";//替换成自己的服务器地址
                SendMessage(url, Cname, Cnum, Cterm, Bluetooth);
            }
        });
    }

    private void SendMessage(String url, final String Cname, String Cnum, String Cterm, String Bluetooth) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Cname", Cname);
        formBuilder.add("Cnum", Cnum);
        formBuilder.add("Cterm", Cterm);
        formBuilder.add("Bluetooth", Bluetooth);
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
                                Toast.makeText(CreateClassActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        titleText=(TextView) findViewById(R.id.class_cid);
                                        titleText.setText(Cid);
                                        Toast.makeText(CreateClassActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateClassActivity.this, "创建失败，课程重复", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateClassActivity.this, "创建失败，内容不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateClassActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateClassActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
            Cid=jsonObject.getString("Cid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}