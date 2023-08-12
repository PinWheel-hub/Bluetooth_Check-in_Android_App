package com.example.app1.bean;

public class Kaoqin {

    private Sinformation student; //学生个人信息
    private String KQState; //考勤状态
    private String name;   //姓名
    private String StuId;  //学号
    private String kqdate; //考勤时间
    private String kqtimes; //考勤次数

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStuId() {
        return this.StuId;
    }
    public void setStuId(String StuId) {
        this.StuId = StuId;
    }

    public String getKqdate() {
        return this.kqdate;
    }
    public void setKqdate(String kqdate) {
        this.kqdate = kqdate;
    }

    public String getKqtimes() {
        return this.kqtimes;
    }
    public void setKqtimes(String kqtimes) {
        this.kqtimes = kqtimes;
    }

    public Sinformation getSinformation() {
        return this.student;
    }
    public void setSinformation(Sinformation student) {
        this.student = student;
    }

    public String getKQState() {
        return this.KQState;
    }
    public void setKQState(String KQState) {
        this.KQState = KQState;
    }

    public Kaoqin(String kqdate,String kqtimes){
        this.kqdate=kqdate;
        this.kqtimes=kqtimes;
    }
}
