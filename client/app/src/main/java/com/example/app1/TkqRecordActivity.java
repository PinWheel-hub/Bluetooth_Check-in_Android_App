package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app1.bean.Course;
import com.example.app1.bean.Kaoqin;
import com.example.app1.bean.Sinformation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TkqRecordActivity extends AppCompatActivity {

    private Bundle bundle;
    private ListView tkqList;//控件
    private TkqlistAdapter adapter;
    private Button titleBack;
    private TextView titleText;

    private String msg_code;
    private String Mark;
    private MyApplication app;
    private List<Kaoqin> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tkq_record);

        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("该学生考勤记录");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app = (MyApplication) getApplication();
                if(app.getjumpflag()=="0") {
                    //返回键跳转到教师查看学生名单的界面
                    Intent intent = new Intent(TkqRecordActivity.this, StuListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(app.getjumpflag()=="1") {
                    //返回键跳转到查看签到情况的界面
                    Intent intent = new Intent(TkqRecordActivity.this, TkqEachCourseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        initData();

        Intent intent=getIntent();//获取Intent对象
        Bundle bundle=intent.getExtras();//获取传递的数据包
        TextView tkqrecord_name=(TextView)findViewById(R.id.tkqrecord_name);//获取到显示姓名的TextView组件
        tkqrecord_name.setText(bundle.getString("Tkq_stuName"));//获取姓名并显示到TextView组件中
        TextView tkqrecord_number=(TextView)findViewById(R.id.tkqrecord_number);//获取到显示学号的TextView组件
        tkqrecord_number.setText(bundle.getString("Tkq_stuNumber"));//获取学号并显示到TextView组件中
    }
    private void initData() {

        tkqList = (ListView)findViewById(R.id.tkqrecord_listview);
        listData=new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String Cid=app.getcurrentcid();
        String Sid=app.getcurrentsid();
        String url=app.gethost()+"/Teacher/get_student_attendance/";
        formBuilder.add("Cid", Cid);
        formBuilder.add("Sid", Sid);
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
                                Toast.makeText(TkqRecordActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        TextView tkqrecord_mark=(TextView)findViewById(R.id.tkqrecord_mark);//获取到显示学号的TextView组件
                                        tkqrecord_mark.setText(Mark);//获取学号并显示到TextView组件中
                                        adapter = new TkqlistAdapter(listData, TkqRecordActivity.this);
                                        tkqList.setAdapter(adapter);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TkqRecordActivity.this, "该课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView tkqrecord_mark=(TextView)findViewById(R.id.tkqrecord_mark);//获取到显示学号的TextView组件
                                        tkqrecord_mark.setText(Mark);//获取学号并显示到TextView组件中
                                        Toast.makeText(TkqRecordActivity.this, "未查询到签到记录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView tkqrecord_mark=(TextView)findViewById(R.id.tkqrecord_mark);//获取到显示学号的TextView组件
                                        tkqrecord_mark.setText(Mark);//获取学号并显示到TextView组件中
                                        Toast.makeText(TkqRecordActivity.this, "当前还未发起过签到", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TkqRecordActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TkqRecordActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }

    public class TkqlistAdapter extends BaseAdapter {

        private List<Kaoqin> listdata;
        private LayoutInflater inflater;

        public TkqlistAdapter() {
        }
        public TkqlistAdapter(List<Kaoqin> KqList, Context context) {
            this.listdata = KqList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() { return listdata.size();}

        @Override
        public Kaoqin getItem(int position) { return listdata.get(position); }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.item_kq_list, null);//这里加载的布局为自己自定义的布局。
            Kaoqin kq1 = getItem(position);

            TextView stu_kqdate = (TextView) view.findViewById(R.id.stu_kqdate);
            TextView stu_kqtimes = (TextView) view.findViewById(R.id.stu_kqtimes);

            stu_kqdate.setText(kq1.getKqdate());
            stu_kqtimes.setText(kq1.getKqtimes());
            return view;
        }
    }

    private void parseJsonWithJsonObject(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code=jsonObject.getString("msg_code");
            Mark=jsonObject.getString("Mark");
            JSONArray student_attendances;
            student_attendances=jsonObject.getJSONArray("student_attendances");
            for(int i=0;i<student_attendances.length();i++)
            {
                JSONObject student_attendance=student_attendances.getJSONObject(i);
                listData.add(new Kaoqin(student_attendance.getString("Datetime"),
                        student_attendance.getString("Atime")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}