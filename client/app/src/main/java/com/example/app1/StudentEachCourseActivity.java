package com.example.app1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app1.bean.Kaoqin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentEachCourseActivity extends AppCompatActivity {

    private static final String TAG = StudentEachCourseActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 112;

    private Button titleBack;
    private TextView titleText;

    private Button mBtnSkqrecord;//查看考勤记录
    private Button quitclass;//查看考勤记录

    private ArrayAdapter<String> arrayList ;
    private TextView mTips;
    private Toast mToast;
    private BlueTooth mbt = new BlueTooth();
    private ListView currentattendance;
    private Adapter adapter;

    private String msg_code;
    private String msg_code1;
    private String msg_code2;
    private String msg_code3;
    private String isSuccess;
    private Boolean flag;
    private Boolean if_in_range;
    private String Cid;
    private String Cname;
    private String Cnum;
    private String Cterm;
    private String Bluetooth;
    private String Tname;
    private MyApplication app;
    private List<Kaoqin> listData;
    private BluetoothAdapter bt;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bt = BluetoothAdapter.getDefaultAdapter();
        isSuccess="0";
        flag=false;
        if_in_range=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_each_course);
        /*
        Intent intent=getIntent();//获取Intent对象
        Bundle bundle=intent.getExtras();//获取传递的数据包
        TextView SKCName=(TextView)findViewById(R.id.SKCName);//获取到显示课程名称的TextView组件
        SKCName.setText(bundle.getString("ClassName"));//获取课程名称并显示到TextView组件中
        TextView SKCteacher=(TextView)findViewById(R.id.SKCteacher);//获取到显示任课教师的TextView组件
        SKCteacher.setText(bundle.getString("ClassTeacher"));//获取任课教师并显示到TextView组件中
        TextView SKCterm=(TextView)findViewById(R.id.SKCterm);//获取到显示课程名称的TextView组件
        SKCterm.setText(bundle.getString("ClassTerm"));//获取课程名称并显示到TextView组件中
        TextView BluetoothAddress = (TextView)findViewById(R.id.SKCBluetooth);
        BluetoothAddress.setText(" ");//显示蓝牙MAC地址
        */
        SendMessage();
        mTips = findViewById(R.id.tv_tips);
        //mbt.turnOnBlueTooth(this, REQUEST_CODE);//打开蓝牙

        titleText=(TextView) findViewById(R.id.title_text);
        titleText.setText("课程信息");
        titleBack=(Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.cancelDiscovery();
                //返回键跳转到学生主页
                Intent intent = new Intent(StudentEachCourseActivity.this,StudentHomeActivity.class);
                startActivity(intent);
            }
        });


        mBtnSkqrecord = (Button) findViewById(R.id.btn_QueryKaoQin);//查看考勤记录
        mBtnSkqrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.cancelDiscovery();
                //跳转到查看考勤记录界面
                Intent intent = new Intent(StudentEachCourseActivity.this,SkqRecordActivity.class);
                startActivity(intent);
            }
        });

        quitclass = (Button) findViewById(R.id.btn_dropout);//退出课程
        quitclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.cancelDiscovery();
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentEachCourseActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确认退出该课程吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendMessage_dropout();
                    }
                });
                builder.show();
            }
        });

        //动态添加权限
//        int MY_PERMISSION_REQUEST_CONSTANT=100;//版本检测
//        if(Build.VERSION.SDK_INT>=6.0){
//            ActivityCompat.requestPermissions(this,new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION
//            },MY_PERMISSION_REQUEST_CONSTANT);
//        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            String[] permissions = new String[]{
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN};
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, REQUEST_CODE );
                }
            }
        }

        initData();

        arrayList= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1);//新建并配置ArrayAapeter
        ListView listView = (ListView) findViewById(R.id.lv_res);//在视图中找到ListView
        listView.setAdapter(arrayList);

        mbt.turnOnBlueTooth(this, REQUEST_CODE);//打开蓝牙
        bt.startDiscovery();
        registerBluetoothReceiver();
    }

    public void signIn(View view){
        if(if_in_range==true) {
            mbt.turnOnBlueTooth(this, REQUEST_CODE);//打开蓝牙
            bt.startDiscovery();
            registerBluetoothReceiver();
        }
        else {
            Toast.makeText(StudentEachCourseActivity.this, "当前不在教师蓝牙范围内", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String text){
        if( mToast == null){
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else{
            mToast.setText(text);
        }
        mToast.show();
    }
    private void registerBluetoothReceiver(){
        num=0;
        IntentFilter filter = new IntentFilter();
        //蓝牙开关状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    //注册广播监听搜索结果
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "Action: "+action);
            num += 1;
            BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (dev == null && num >= 2) {
                isSuccess = "0";
                if (flag == true) {
                    SendMessage_startsign();
                } else {
                    if_in_range = false;
                    Toast.makeText(StudentEachCourseActivity.this, "当前不在教师蓝牙范围内", Toast.LENGTH_SHORT).show();
                    flag = true;
                }
                bt.cancelDiscovery();
                unregisterReceiver(mReceiver);
            }
            else if (dev != null) {
                if(flag==false && num == 2)
                    showToast("开始查找");
                else if(num == 2)
                    showToast("开始签到");
                Log.i(TAG, "BluetoothDevice: " +num+", "+ dev.getName() + ", " + dev.getAddress());
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        Log.i(TAG, "STATE: " + state);
                        mTips.setText("蓝牙状态改变");
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        bt.cancelDiscovery();
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        assert dev != null;

                        String myaddress = Bluetooth;
                        if (dev.getAddress().equals(myaddress)) {
                            isSuccess = "1";
                            if (flag == true) {
                                SendMessage_startsign();
                            } else {
                                if_in_range = true;
                                Button studentsign = (Button) findViewById(R.id.btn_studentsign);
                                studentsign.setBackgroundColor(Color.parseColor("#BC90ED"));
                                flag = true;
                            }
                            bt.cancelDiscovery();
                            unregisterReceiver(mReceiver);
                        }
                        break;
                }
            }
        }
    };

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
                                Toast.makeText(StudentEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        TextView KCCode=(TextView)findViewById(R.id.SKCCode);//获取到显示课程名称的TextView组件
                                        KCCode.setText(Cid);//获取课程名称并显示到TextView组件中
                                        TextView KCName=(TextView)findViewById(R.id.SKCName);//获取到显示课程名称的TextView组件
                                        KCName.setText(Cname);//获取课程名称并显示到TextView组件中
                                        TextView KCteacher=(TextView)findViewById(R.id.SKCteacher);//获取到显示任课教师的TextView组件
                                        KCteacher.setText(Tname);//获取任课教师并显示到TextView组件中
                                        TextView KCnum=(TextView)findViewById(R.id.SKCnum);//获取到显示任课教师的TextView组件
                                        KCnum.setText(Cnum);//获取任课教师并显示到TextView组件中
                                        TextView KCterm=(TextView)findViewById(R.id.SKCterm);//获取到显示课程名称的TextView组件
                                        KCterm.setText(Cterm);//获取课程名称并显示到TextView组件中
                                        TextView BluetoothAddress = (TextView)findViewById(R.id.SKCBluetooth);
                                        BluetoothAddress.setText(Bluetooth);//显示蓝牙MAC地址
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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

    private void SendMessage_dropout() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/student_curriculum/";
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
                                Toast.makeText(StudentEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(StudentEachCourseActivity.this, "退课成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(StudentEachCourseActivity.this,StudentHomeActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "您已退出该课程", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
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

    private void initData() {

        currentattendance = (ListView)findViewById(R.id.lv_res);
        listData=new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String Cid=app.getcurrentcid();
        String url=app.gethost()+"/Student/get_current_attendence/";
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
                                Toast.makeText(StudentEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
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
                                        adapter = new Adapter(listData, StudentEachCourseActivity.this);
                                        currentattendance.setAdapter(adapter);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        titleText=(TextView) findViewById(R.id.tv_tips);
                                        titleText.setText("最新签到结果： 该课程还未发起过签到");
                                    }
                                });

                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        titleText=(TextView) findViewById(R.id.tv_tips);
                                        titleText.setText("最新签到结果： 存在未完成的签到");
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }

    public class Adapter extends BaseAdapter {

        private List<Kaoqin> listdata;
        private LayoutInflater inflater;

        public Adapter() {
        }
        public Adapter(List<Kaoqin> KqList, Context context) {
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

    private void parseJsonWithJsonObject2(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code2=jsonObject.getString("msg_code");
            listData.add(new Kaoqin(jsonObject.getString("Datetime"),
                    jsonObject.getString("Atime")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SendMessage_startsign() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        app = (MyApplication) getApplication();
        String url=app.gethost()+"/student_attendance/";
        Cid=app.getcurrentcid();
        formBuilder.add("Cid", Cid);
        formBuilder.add("isSuccess", isSuccess);
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
                                Toast.makeText(StudentEachCourseActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                parseJsonWithJsonObject3(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (msg_code3) {
                            case "0":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "课程已不存在", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "1":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "您已退出该课程", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "目前还未发起签到", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "4":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "您已成功签到", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "5":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "签到失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "6":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "该考勤已经过期", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case "7":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "当前登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(StudentEachCourseActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });
            }
        });
    }

    private void parseJsonWithJsonObject3(Response response) throws IOException {
        String res = response.body().string();
        try{
            JSONObject jsonObject=new JSONObject(res);
            msg_code3=jsonObject.getString("msg_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}