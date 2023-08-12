package com.example.app1.bean;

public class Sinformation {
    private String sid;//用户名/手机号
    private String telephone;//用户名/手机号
    private String password; //密码
    private String school; //学校
    private String name;//姓名
    private String number; //学号或工号
    private String mark; //学号或工号

    public Sinformation(){ }

    public Sinformation(String sid, String name,String number, String mark){
        this.sid=sid;
        this.name=name;
        this.number=number;
        this.mark=mark;
    }

    public Sinformation(String sid, String name,String number){
        this.sid=sid;
        this.name=name;
        this.number=number;
    }

    public String getSid() {
        return this.sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTelephone() {
        return this.telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchool() {
        return this.school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

    public String getName(){
        return this.name;
    }
    public  void setName(String name){
        this.name = name;
    }

    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getMark() {
        return this.mark;
    }
    public void setMark(String mark) {
        this.mark = mark;
    }
}
