package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app1.bean.Course;
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

public class StuListActivity extends AppCompatActivity {

    private Bundle bundle;
    private ListView stuList;//listview控件
    private StulistAdapter adapter;
    private Button titleBack;
    private TextView titleText;

    private String msg_code;
    private MyApplication app;
    List<Sinformation> listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_list);

        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("学生名单");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师查看课程的界面
                //Intent intent = new Intent(StuListActivity.this,TeacherEachCourseActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        bundle = intent.getExtras();

        stuList = (ListView) findViewById(R.id.listview_stulist);
        initData();
        //StulistAdapter adapter = new StulistAdapter();
        //stuList.setAdapter(adapter);


        stuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //点击其中一个item，跳转到查看学生该门课程考勤的界面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StuListActivity.this, TkqRecordActivity.class);
                app = (MyApplication) getApplication();
                app.setjumpflag("0");
                app.setcurrentsid(listData.get(position).getSid());
                TextView ItemStuName = (TextView) view.findViewById(R.id.stu_list_name);
                TextView ItemStuId = (TextView) view.findViewById(R.id.stu_list_number);
                TextView ItemStuMark = (TextView) view.findViewById(R.id.stu_list_mark);
                Bundle bundle = new Bundle();//创建并实例化一个Bundle对象
                bundle.putCharSequence("Tkq_stuName", ItemStuName.getText().toString());
                bundle.putCharSequence("Tkq_stuNumber", ItemStuId.getText().toString());
                bundle.putCharSequence("Tkq_stuMark", ItemStuMark.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData() {

        stuList = (ListView)findViewById(R.id.listview_stulist);
        listData=new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/Teacher/get_curriculum_students/";
        String Cid=app.getcurrentcid();
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
                                Toast.makeText(StuListActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        adapter = new StulistAdapter(listData, StuListActivity.this);
                                        stuList.setAdapter(adapter);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StuListActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StuListActivity.this, "目前未有学生选课", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StuListActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }

    public class StulistAdapter extends BaseAdapter {

        private List<Sinformation> listdata;
        private LayoutInflater inflater;

        public StulistAdapter() {
        }
        public StulistAdapter(List<Sinformation> StuList, Context context) {
            this.listdata = StuList;
            this.inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Sinformation getItem(int position) {
            return listdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.item_stu_list, null);//这里加载的布局为自己自定义的布局。
            Sinformation stu1 = getItem(position);

            TextView stu_list_name = (TextView) view.findViewById(R.id.stu_list_name);
            TextView stu_list_number = (TextView) view.findViewById(R.id.stu_list_number);
            TextView stu_list_mark = (TextView) view.findViewById(R.id.stu_list_mark);

            stu_list_name.setText(stu1.getName());
            stu_list_number.setText(stu1.getNumber());
            stu_list_mark.setText(stu1.getMark());
            return view;
        }
    }

    private void parseJsonWithJsonObject(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code=jsonObject.getString("msg_code");
            JSONArray students;
            students=jsonObject.getJSONArray("students");
            for(int i=0;i<students.length();i++)
            {
                JSONObject student=students.getJSONObject(i);
                listData.add(new Sinformation(student.getString("Sid"),
                        student.getString("Sname"),
                        student.getString("Snum"),
                        student.getString("Mark")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

