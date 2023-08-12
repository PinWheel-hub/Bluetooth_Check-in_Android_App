package com.example.app1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class TeacherHomeActivity extends AppCompatActivity {

    private Button titleBack;
    private TextView titleText;

    private Button mBtnCreateClass;
    private Button mBtnTChange;
    private ListView tcourse_list; //控件
    private TcAdapter adapter;
    private Bundle bundle;
    private String msg_code;
    private String msg_code1;
    private String Tnum;
    private String Tname;
    private String Tschool;
    private MyApplication app;
    private List<Course> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        Intent intent = getIntent();
        bundle = intent.getExtras();

        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("教师主页");
        SendMessage();
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除课程
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherHomeActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确认退出登录吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //返回键跳转到教师登录界面
                        Intent intent = new Intent(TeacherHomeActivity.this,Logint.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

        mBtnCreateClass = (Button) findViewById(R.id.btn_createclass);
        mBtnCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到创建课程界面
                Intent intent = new Intent(TeacherHomeActivity.this, CreateClassActivity.class);
                startActivity(intent);
            }
        });
        mBtnTChange = (Button) findViewById(R.id.btn_tchange);
        mBtnTChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到老师修改个人信息界面
                Intent intent = new Intent(TeacherHomeActivity.this, TchangeActivity.class);
                startActivity(intent);
            }
        });

        tcourse_list = (ListView) findViewById(R.id.tcourse_listview);
        initData();
        tcourse_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeacherHomeActivity.this, TeacherEachCourseActivity.class);
                app = (MyApplication) getApplication();
                app.setcurrentcid(listData.get(position).getCoursecode());
                Log.i("info_s", "Cid is  :" + app.getcurrentcid());
                //跳转到教师操作每门课程的界面
                TextView ItemTerm = (TextView) view.findViewById(R.id.tcourselist_term); //开课学期
                TextView ItemClassName = (TextView) view.findViewById(R.id.tcourselist_name);  //课程名称
                TextView ItemClassNum = (TextView) view.findViewById(R.id.tcourselist_num); //教务代码
                //TextView ItemClassTeacher = (TextView) view.findViewById(R.id.tcourselist_teacher); //任课教师

                Bundle bundle = new Bundle();
                bundle.putCharSequence("ClassTerm", ItemTerm.getText().toString());
                bundle.putCharSequence("ClassName", ItemClassName.getText().toString());
                //bundle.putCharSequence("ClassTeacher", ItemClassTeacher.getText().toString());
                bundle.putCharSequence("ClassNum", ItemClassNum.getText().toString());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        //初始化集合中的数据
        tcourse_list = (ListView) findViewById(R.id.tcourse_listview);
        listData=new ArrayList<>();
        listData = new ArrayList<Course>(); //List集合数据

        OkHttpClient client = new OkHttpClient();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/teacher_curriculum/";
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
                                Toast.makeText(TeacherHomeActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        adapter = new TcAdapter(listData, TeacherHomeActivity.this);
                                        tcourse_list.setAdapter(adapter);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherHomeActivity.this, "您还未创建课程", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherHomeActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherHomeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }

    public class TcAdapter extends BaseAdapter {
        private List<Course> listdata;
        private LayoutInflater inflater;

        public TcAdapter() {
        }
        public TcAdapter(List<Course> courseList, Context context) {
            this.listdata = courseList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Course getItem(int position) {
            return listdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_tcourse_list, null);//这里加载的布局为自己自定义的布局。
            Course course1 = getItem(position);

            TextView tcourselist_name = (TextView) view.findViewById(R.id.tcourselist_name);
            //TextView tcourselist_teacher = (TextView) view.findViewById(R.id.tcourselist_teacher);
            TextView tcourselist_num = (TextView) view.findViewById(R.id.tcourselist_num);
            TextView tcourselist_term = (TextView) view.findViewById(R.id.tcourselist_term);

            tcourselist_name.setText(course1.getCoursename());
            //tcourselist_teacher.setText(course1.getTeachername());
            tcourselist_num.setText(course1.getCoursenum());
            tcourselist_term.setText(course1.getCourseterm());
            return view;
        }

    }

    private void SendMessage() {
        OkHttpClient client = new OkHttpClient();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/teacher_restful/";
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
                                Toast.makeText(TeacherHomeActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        titleText=(TextView) findViewById(R.id.User_tcollege);
                                        titleText.setText(Tschool);
                                        titleText=(TextView) findViewById(R.id.User_tnumber);
                                        titleText.setText(Tnum);
                                        titleText=(TextView) findViewById(R.id.User_tname);
                                        titleText.setText(Tname);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherHomeActivity.this, "用户查询错误", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherHomeActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TeacherHomeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
            Tnum=jsonObject.getString("Tnum");
            Tname=jsonObject.getString("Tname");
            Tschool=jsonObject.getString("Tschool");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonWithJsonObject1(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code1=jsonObject.getString("msg_code");
            JSONArray curriculums;
            curriculums=jsonObject.getJSONArray("curriculums");
            for(int i=0;i<curriculums.length();i++)
            {
                JSONObject curriculum=curriculums.getJSONObject(i);
                listData.add(new Course(curriculum.getString("Cname"),
                        curriculum.getString("Cnum"),
                        curriculum.getString("Cterm"),
                        curriculum.getString("Cid"),
                        Tname));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

