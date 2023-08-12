package com.example.app1;

import android.app.Application;
//存放全局变量
public class MyApplication extends Application
{
    private String sessionid;
    private String currentcid;
    private String currentsid;
    private String currentatime;
    private String host;
    private String jumpflag;
    @Override
    public void onCreate() {
        super.onCreate();
        setsessionid(""); //初始化全局变量
        setcurrentcid("");
        setcurrentsid("");
        setcurrentatime("");
        setjumpflag("0");
        sethost("http://106.14.2.150:5000");
    }
    public String getsessionid() {
        return sessionid;
    }
    public void setsessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getcurrentcid() {
        return currentcid;
    }
    public void setcurrentcid(String currentcid) {
        this.currentcid = currentcid;
    }

    public String getcurrentsid() {
        return currentsid;
    }
    public void setcurrentsid(String currentsid) {
        this.currentsid = currentsid;
    }

    public String getcurrentatime() {
        return currentatime;
    }
    public void setcurrentatime(String currentatime) {
        this.currentatime = currentatime;
    }

    public String gethost() {
        return host;
    }
    public void sethost(String host) {
        this.host = host;
    }

    public String getjumpflag() {
        return jumpflag;
    }
    public void setjumpflag(String jumpflag) {
        this.jumpflag = jumpflag;
    }

    private static final String NAME = "com.example.app1.MyApplication";
}
