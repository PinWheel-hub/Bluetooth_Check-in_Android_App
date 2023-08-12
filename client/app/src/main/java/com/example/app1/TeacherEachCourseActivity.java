package com.example.app1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherEachCourseActivity extends AppCompatActivity {

    private static final String TAG = null;
    private Button titleBack;
    private TextView titleText;

    private Button mBtnChangeclass;
    private Button mBtnStulist;
    private Button mBtnKQEachcourse;
    private Button mBtnStartsign;
    private Button mBtnDelete;

    private String msg_code;
    private String msg_code1;
    private String msg_code2;
    private String Cid;
    private String Cname;
    private String Cnum;
    private String Cterm;
    private String Bluetooth;
    private String Tname;
    private String Atime;
    private MyApplication app;

    int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_each_course);
        /*
        Intent intent=getIntent();//获取Intent对象
        Bundle bundle=intent.getExtras();//获取传递的数据包
        TextView KCName=(TextView)findViewById(R.id.KCName);//获取到显示课程名称的TextView组件
        KCName.setText(bundle.getString("ClassName"));//获取课程名称并显示到TextView组件中
        TextView KCteacher=(TextView)findViewById(R.id.KCteacher);//获取到显示任课教师的TextView组件
        KCteacher.setText(bundle.getString("ClassTeacher"));//获取任课教师并显示到TextView组件中
        TextView KCnum=(TextView)findViewById(R.id.KCnum);//获取到显示任课教师的TextView组件
        KCnum.setText(bundle.getString("ClassNum"));//获取任课教师并显示到TextView组件中
        TextView KCterm=(TextView)findViewById(R.id.KCterm);//获取到显示课程名称的TextView组件
        KCterm.setText(bundle.getString("ClassTerm"));//获取课程名称并显示到TextView组件中
        TextView BluetoothAddress = (TextView)findViewById(R.id.KCBluetoothAddress);
        BluetoothAddress.setText(" ");//显示蓝牙MAC地址
        */
        SendMessage();
        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("课程信息");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师主页
                Intent intent = new Intent(TeacherEachCourseActivity.this,TeacherHomeActivity.class);
                startActivity(intent);
            }
        });

        mBtnChangeclass = (Button) findViewById(R.id.btn_change_class);
        mBtnChangeclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到教师修改课程信息界面
                Intent intent = new Intent(TeacherEachCourseActivity.this,ChangeClassActivity.class);
                startActivity(intent);
            }
        });
        mBtnDelete = (Button) findViewById(R.id.btn_delete_class);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除课程
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherEachCourseActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确认删除该课程吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendMessage_deleteclass();
                    }
                });
                builder.show();
            }
        });
        mBtnStulist = (Button) findViewById(R.id.btn_student_list);
        mBtnStulist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到学生名单界面
                Intent intent = new Intent(TeacherEachCourseActivity.this,StuListActivity.class);
                startActivity(intent);
            }
        });
        mBtnKQEachcourse = (Button) findViewById(R.id.btn_QueryKaoQin);
        mBtnKQEachcourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到当前课程考勤名单界面
                Intent intent = new Intent(TeacherEachCourseActivity.this,TkqEachCourseActivity.class);
                startActivity(intent);
            }
        });
        mBtnStartsign = (Button) findViewById(R.id.btn_startsign);
        mBtnStartsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoverableIntent, REQUEST_CODE);
            }
        });



        //动态添加权限
        int MY_PERMISSION_REQUEST_CONSTANT=100;//版本检测
        if(Build.VERSION.SDK_INT>=6.0){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CONSTANT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            if (resultCode != RESULT_CANCELED) {
                SendMessage_startsign();
            }
        }
    }

    private void SendMessage() {
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
                                Toast.makeText(TeacherEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        TextView KCCode=(TextView)findViewById(R.id.KCCode);//获取到显示课程名称的TextView组件
                                        KCCode.setText(Cid);//获取课程名称并显示到TextView组件中
                                        TextView KCName=(TextView)findViewById(R.id.KCName);//获取到显示课程名称的TextView组件
                                        KCName.setText(Cname);//获取课程名称并显示到TextView组件中
                                        TextView KCteacher=(TextView)findViewById(R.id.KCteacher);//获取到显示任课教师的TextView组件
                                        KCteacher.setText(Tname);//获取任课教师并显示到TextView组件中
                                        TextView KCnum=(TextView)findViewById(R.id.KCnum);//获取到显示任课教师的TextView组件
                                        KCnum.setText(Cnum);//获取任课教师并显示到TextView组件中
                                        TextView KCterm=(TextView)findViewById(R.id.KCterm);//获取到显示课程名称的TextView组件
                                        KCterm.setText(Cterm);//获取课程名称并显示到TextView组件中
                                        TextView BluetoothAddress = (TextView)findViewById(R.id.KCBluetoothAddress);
                                        BluetoothAddress.setText(Bluetooth);//显示蓝牙MAC地址
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
            Cname=jsonObject.getString("Cname");
            Cnum=jsonObject.getString("Cnum");
            Cterm=jsonObject.getString("Cterm");
            Bluetooth=jsonObject.getString("Bluetooth");
            Tname=jsonObject.getString("Tname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SendMessage_deleteclass() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/teacher_curriculum/";
        Cid=app.getcurrentcid();
        formBuilder.add("Cid", Cid);
        Request request = new Request.Builder().url(url)
                .addHeader("Cookie", "session=" + app.getsessionid())
                .delete(formBuilder.build()).build();
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
                                Toast.makeText(TeacherEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(TeacherEachCourseActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(TeacherEachCourseActivity.this,TeacherHomeActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "课程已删除", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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

    private void SendMessage_startsign() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/teacher_attendance/";
        Cid=app.getcurrentcid();
        formBuilder.add("Cid", Cid);
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
                                Toast.makeText(TeacherEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                parseJsonWithJsonObject2(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (msg_code2) {
                            case "1":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        app = (MyApplication) getApplication();
                                        app.setcurrentatime(Atime);
                                        Toast.makeText(TeacherEachCourseActivity.this, "考勤发起成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "考勤记录出错", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "当前正在进行考勤", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }

    private void parseJsonWithJsonObject2(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code2=jsonObject.getString("msg_code");
            Atime=jsonObject.getString("Atime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}