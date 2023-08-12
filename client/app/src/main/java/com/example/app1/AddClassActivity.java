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

public class AddClassActivity extends AppCompatActivity {

    private Button titleBack;
    private Button search;
    private Button add;
    private TextView titleText;
    private EditText cid;
    private String Cname;
    private String Cnum;
    private String Cterm;
    private String Tname;
    private String Cid;
    private String Bluetooth;
    private String msg_code;
    private String msg_code1;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        cid = findViewById(R.id.et_sclasscode);
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("添加课程");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到学生主页
                Intent intent = new Intent(AddClassActivity.this,StudentHomeActivity.class);
                startActivity(intent);
            }
        });

        search=(Button) findViewById(R.id.btn_ssearchcode);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Cid = String.valueOf(cid.getText());
                app = (MyApplication) getApplication();
                String url = app.gethost()+"/get_curriculum/";//替换成自己的服务器地址
                SendMessage(url, Cid);
            }
        });

        add=(Button) findViewById(R.id.btn_saddclass);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage_addclass();
            }
        });
    }

    private void SendMessage(String url, final String Cid) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Cid", Cid);
        app = (MyApplication) getApplication();
        Request request = new Request.Builder().url(url)
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
                                Toast.makeText(AddClassActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        titleText=(TextView) findViewById(R.id.et_sfindcode);
                                        titleText.setText(Cid);
                                        titleText=(TextView) findViewById(R.id.et_sfindname);
                                        titleText.setText(Cname);
                                        titleText=(TextView) findViewById(R.id.et_sfindnum);
                                        titleText.setText(Cnum);
                                        titleText=(TextView) findViewById(R.id.et_sfindteacher);
                                        titleText.setText(Tname);
                                        titleText=(TextView) findViewById(R.id.et_sfindterm);
                                        titleText.setText(Cterm);
                                        titleText=(TextView) findViewById(R.id.et_sfindbluetooth);
                                        titleText.setText(Bluetooth);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddClassActivity.this, "目标课程不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddClassActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
            Cname=jsonObject.getString("Cname");
            Cnum=jsonObject.getString("Cnum");
            Cterm=jsonObject.getString("Cterm");
            Tname=jsonObject.getString("Tname");
            Bluetooth=jsonObject.getString("Bluetooth");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SendMessage_addclass() {
        app = (MyApplication) getApplication();
        String url = app.gethost()+"/student_curriculum/";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        if(Cid!=null)
            formBuilder.add("Cid", Cid);
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
                                Toast.makeText(AddClassActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(AddClassActivity.this, "选课成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddClassActivity.this, "课程不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddClassActivity.this, "您已选上该课程", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddClassActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddClassActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}