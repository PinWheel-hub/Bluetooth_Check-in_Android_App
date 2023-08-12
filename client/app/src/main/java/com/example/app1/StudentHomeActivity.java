package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;
import com.example.app1.bean.Course;

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

public class StudentHomeActivity extends AppCompatActivity {

    private Button titleBack;
    private TextView titleText;

    private Button mBtnSChange;
    private Button mBtnAddClass;
    private ListView scourse_list; //控件
    private ScAdapter adapter;
    private Bundle bundle;
    private String msg_code;
    private String msg_code1;
    private String Snum;
    private String Sname;
    private String Sschool;
    private MyApplication app;
    private List<Course> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("学生主页");
        SendMessage();
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除课程
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentHomeActivity.this);
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
                        //返回键跳转到学生登录界面
                        Intent intent = new Intent(StudentHomeActivity.this,Logins.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

        mBtnSChange = (Button) findViewById(R.id.btn_schange);
        mBtnSChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到学生修改个人信息界面
                Intent intent = new Intent(StudentHomeActivity.this,SChangeActivity.class);
                startActivity(intent);
            }
        });
        mBtnAddClass = (Button) findViewById(R.id.btn_saddcourse);
        mBtnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到学生修改个人信息界面
                Intent intent = new Intent(StudentHomeActivity.this,AddClassActivity.class);
                startActivity(intent);
            }
        });
        scourse_list = (ListView) findViewById(R.id.scourse_listview);
        initData();
        scourse_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentEachCourseActivity.class);
                app = (MyApplication) getApplication();
                app.setcurrentcid(listData.get(position).getCoursecode());
                Log.i("info_s", "Cid is  :" + app.getcurrentcid());
                //跳转到学生操作每门课程的界面
                TextView ItemTerm = (TextView) view.findViewById(R.id.scourselist_term); //开课学期
                TextView ItemClassName = (TextView) view.findViewById(R.id.scourselist_name);  //课程名称
                TextView ItemClassTeacher = (TextView) view.findViewById(R.id.scourselist_teacher); //任课教师

                Bundle bundle = new Bundle();
                bundle.putCharSequence("ClassTerm", ItemTerm.getText().toString());
                bundle.putCharSequence("ClassName", ItemClassName.getText().toString());
                bundle.putCharSequence("ClassTeacher", ItemClassTeacher.getText().toString());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void initData() {
        //初始化集合中的数据
        scourse_list = (ListView) findViewById(R.id.scourse_listview);
        listData=new ArrayList<>();
        listData = new ArrayList<Course>(); //List集合数据

        OkHttpClient client = new OkHttpClient();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/student_curriculum/";
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
                                Toast.makeText(StudentHomeActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        adapter = new ScAdapter(listData, StudentHomeActivity.this);
                                        scourse_list.setAdapter(adapter);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentHomeActivity.this, "您还未选课程", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentHomeActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentHomeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }
    public class ScAdapter extends BaseAdapter {
        private List<Course> listdata;
        private LayoutInflater inflater;

        public ScAdapter() {
        }
        public ScAdapter(List<Course> courseList, Context context) {
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
            View view = inflater.inflate(R.layout.item_scourse_list, null);//这里加载的布局为自己自定义的布局。
            Course course1 = getItem(position);

            TextView scourselist_name = (TextView) view.findViewById(R.id.scourselist_name);
            TextView scourselist_teacher = (TextView) view.findViewById(R.id.scourselist_teacher);
            TextView scourselist_term = (TextView) view.findViewById(R.id.scourselist_term);

            scourselist_name.setText(course1.getCoursename());
            scourselist_teacher.setText(course1.getTeachername());
            scourselist_term.setText(course1.getCourseterm());
            return view;
        }

    }

    private void SendMessage() {
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
                                Toast.makeText(StudentHomeActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        titleText=(TextView) findViewById(R.id.User_scollege);
                                        titleText.setText(Sschool);
                                        titleText=(TextView) findViewById(R.id.User_snumber);
                                        titleText.setText(Snum);
                                        titleText=(TextView) findViewById(R.id.User_sname);
                                        titleText.setText(Sname);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentHomeActivity.this, "用户查询错误", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentHomeActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentHomeActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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
            Snum=jsonObject.getString("Snum");
            Sname=jsonObject.getString("Sname");
            Sschool=jsonObject.getString("Sschool");
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
                        curriculum.getString("Tname")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
