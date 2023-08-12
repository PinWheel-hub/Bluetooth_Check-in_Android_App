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

public class ChangeClassActivity extends AppCompatActivity {

    private Button titleBack;
    private Button changeclass;
    private TextView titleText;
    private EditText name;
    private EditText num;
    private EditText term;
    private EditText bluetooth;

    private String Cid;
    private String current_name;
    private String current_num;
    private String current_term;
    private String current_bluetooth;
    private String msg_code;
    private String msg_code1;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_class);
        SendMessage_currentinformation();
        name = findViewById(R.id.class_namec);
        num = findViewById(R.id.class_numc);
        term = findViewById(R.id.class_termc);
        bluetooth = findViewById(R.id.class_bluetoothc);
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("修改课程信息");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师主页
                Intent intent = new Intent(ChangeClassActivity.this,TeacherEachCourseActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        changeclass=(Button) findViewById(R.id.btn_changeclassfinish);
        changeclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Cname = String.valueOf(name.getText());
                String Cnum = String.valueOf(num.getText());
                String Cterm = String.valueOf(term.getText());
                String Bluetooth = String.valueOf(bluetooth.getText());
                app = (MyApplication) getApplication();
                String url = app.gethost()+"/teacher_curriculum/";//替换成自己的服务器地址
                SendMessage(url, app.getcurrentcid(), Cname, Cnum, Cterm, Bluetooth);
            }
        });
    }

    private void SendMessage(String url, final String Cid, String Cname, String Cnum, String Cterm, String Bluetooth) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Cid", Cid);
        formBuilder.add("Cname", Cname);
        formBuilder.add("Cnum", Cnum);
        formBuilder.add("Cterm", Cterm);
        formBuilder.add("Bluetooth", Bluetooth);
        app = (MyApplication) getApplication();
        Request request = new Request.Builder().url(url)
                .addHeader("Cookie", "session=" + app.getsessionid())
                .put(formBuilder.build()).build();
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
                                Toast.makeText(ChangeClassActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(ChangeClassActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "该课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "修改失败，教务代码重复", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "修改失败，内容不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/get_curriculum/";
        Cid=app.getcurrentcid();
        formBuilder.add("Cid", Cid);
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
                                Toast.makeText(ChangeClassActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        name.setText(current_name);
                                        num.setText(current_num);
                                        term.setText(current_term);
                                        bluetooth.setText(current_bluetooth);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeClassActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
            current_name=jsonObject.getString("Cname");
            current_num=jsonObject.getString("Cnum");
            current_term=jsonObject.getString("Cterm");
            current_bluetooth=jsonObject.getString("Bluetooth");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}