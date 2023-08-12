package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
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

public class TkqEachCourseActivity extends AppCompatActivity {

    private Bundle bundle;
    private ListView unsignedlist,signedlist;//控件
    private SignedAdapter adapter1;
    private UnsignedAdapter adapter2;
    private Button titleBack;
    private Button refresh;
    private TextView titleText;

    private String msg_code;
    private String Atime;
    private MyApplication app;
    private List<Sinformation> listData1;
    private List<Sinformation> listData2;
    /*
    public String[] unsignednames = {"小明", "小王"};
    public String[] unsignednumbers= {"2018110000", "2018110001"};
    public String[] signednames = {"小红", "小张"};
    public String[] signednumbers= {"2018110002", "2018110003"};
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tkq_each_course);

        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("最新签到情况");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回键跳转到教师查看课程界面
                Intent intent = new Intent(TkqEachCourseActivity.this,TeacherEachCourseActivity.class);
                startActivity(intent);
            }
        });

        refresh=(Button) findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TkqEachCourseActivity.this,TkqEachCourseActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        bundle = intent.getExtras();

        unsignedlist = (ListView) findViewById(R.id.listview_unsigned);
        //UnsignedAdapter adapter1 = new UnsignedAdapter();
        //unsignedlist.setAdapter(adapter1);

        signedlist = (ListView) findViewById(R.id.listview_signed);
        //SignedAdapter adapter2 = new SignedAdapter();
        //signedlist.setAdapter(adapter2);
        initData();

        //StulistAdapter adapter = new StulistAdapter();
        //stuList.setAdapter(adapter);


        unsignedlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //点击其中一个item，跳转到查看学生该门课程考勤的界面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TkqEachCourseActivity.this, TkqRecordActivity.class);
                app = (MyApplication) getApplication();
                app.setjumpflag("1");
                app.setcurrentsid(listData2.get(position).getSid());
                TextView ItemStuName = (TextView) view.findViewById(R.id.stu_list_name);
                TextView ItemStuId = (TextView) view.findViewById(R.id.stu_list_number);
                Bundle bundle = new Bundle();//创建并实例化一个Bundle对象
                bundle.putCharSequence("Tkq_stuName", ItemStuName.getText().toString());
                bundle.putCharSequence("Tkq_stuNumber", ItemStuId.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        signedlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //点击其中一个item，跳转到查看学生该门课程考勤的界面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TkqEachCourseActivity.this, TkqRecordActivity.class);
                app = (MyApplication) getApplication();
                app.setjumpflag("1");
                app.setcurrentsid(listData1.get(position).getSid());
                TextView ItemStuName = (TextView) view.findViewById(R.id.stu_list_name);
                TextView ItemStuId = (TextView) view.findViewById(R.id.stu_list_number);
                Bundle bundle = new Bundle();//创建并实例化一个Bundle对象
                bundle.putCharSequence("Tkq_stuName", ItemStuName.getText().toString());
                bundle.putCharSequence("Tkq_stuNumber", ItemStuId.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initData() {

        unsignedlist = (ListView) findViewById(R.id.listview_unsigned);
        listData1=new ArrayList<>();

        signedlist = (ListView) findViewById(R.id.listview_signed);
        listData2=new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/attendance_situation/";
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
                                Toast.makeText(TkqEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        adapter2 = new UnsignedAdapter(listData2, TkqEachCourseActivity.this);
                                        unsignedlist.setAdapter(adapter2);
                                        adapter1 = new SignedAdapter(listData1, TkqEachCourseActivity.this);
                                        signedlist.setAdapter(adapter1);
                                        titleText=(TextView) findViewById(R.id.title_text);
                                        titleText.setText("最新签到情况(当前签到次数："+Atime+")");
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TkqEachCourseActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        titleText=(TextView) findViewById(R.id.title_text);
                                        titleText.setText("最新签到情况(目前没有发起的签到)");
                                    }
                                });

                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        titleText=(TextView) findViewById(R.id.title_text);
                                        titleText.setText("最新签到情况(当前签到次数："+Atime+")");
                                        Toast.makeText(TkqEachCourseActivity.this, "目前未有学生选课", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TkqEachCourseActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TkqEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });

    }
    public class UnsignedAdapter extends BaseAdapter {

        private List<Sinformation> listdata;
        private LayoutInflater inflater;

        public UnsignedAdapter() {
        }
        public UnsignedAdapter(List<Sinformation> StuList, Context context) {
            this.listdata = StuList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() { return listdata.size(); }

        @Override
        public Sinformation getItem(int position) { return listdata.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.item_stu_list1, null);//这里加载的布局为自己自定义的布局。
            Sinformation stu1 = getItem(position);

            TextView stu_list_name = (TextView) view.findViewById(R.id.stu_list_name);
            TextView stu_list_number = (TextView) view.findViewById(R.id.stu_list_number);

            stu_list_name.setText(stu1.getName());
            stu_list_number.setText(stu1.getNumber());
            return view;
        }

    }
    public class SignedAdapter extends BaseAdapter {

        private List<Sinformation> listdata;
        private LayoutInflater inflater;

        public SignedAdapter() {
        }
        public SignedAdapter(List<Sinformation> StuList, Context context) {
            this.listdata = StuList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() { return listdata.size(); }

        @Override
        public Sinformation getItem(int position) { return listdata.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.item_stu_list1, null);//这里加载的布局为自己自定义的布局。
            Sinformation stu2 = getItem(position);

            TextView stu_list_name = (TextView) view.findViewById(R.id.stu_list_name);
            TextView stu_list_number = (TextView) view.findViewById(R.id.stu_list_number);

            stu_list_name.setText(stu2.getName());
            stu_list_number.setText(stu2.getNumber());
            return view;
        }
    }

    private void parseJsonWithJsonObject(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code=jsonObject.getString("msg_code");
            Atime=jsonObject.getString("Atime");
            JSONArray signed_students, unsigned_students;
            signed_students=jsonObject.getJSONArray("signed_students");
            unsigned_students=jsonObject.getJSONArray("unsigned_students");
            for(int i=0;i<signed_students.length();i++)
            {
                JSONObject signed_student=signed_students.getJSONObject(i);
                listData1.add(new Sinformation(signed_student.getString("Sid"),
                        signed_student.getString("Sname"),
                        signed_student.getString("Snum")));
            }
            for(int i=0;i<unsigned_students.length();i++)
            {
                JSONObject unsigned_student=unsigned_students.getJSONObject(i);
                listData2.add(new Sinformation(unsigned_student.getString("Sid"),
                        unsigned_student.getString("Sname"),
                        unsigned_student.getString("Snum")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}