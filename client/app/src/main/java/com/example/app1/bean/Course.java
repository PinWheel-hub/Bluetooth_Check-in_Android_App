package com.example.app1.bean;

public class Course {

    private String coursecode;//课程代码
    private String courseterm;//开课学期
    private String coursename;//课程名称
    private String coursenum;//课程名称
    private String BlueToothAddres; //蓝牙地址
    private String teachername;//任课教师
    private Sinformation student;

    public Course(){ }

    public Course(String coursename,String coursenum,String courseterm,String coursecode,String teachername){
        this.coursename=coursename;
        this.coursenum=coursenum;
        this.courseterm=courseterm;
        this.coursecode=coursecode;
        this.teachername=teachername;
    }

    public String getTeachername(){
        return teachername;
    } //获得任课教师
    public void setTeachername(String Teachername){
        this.teachername = Teachername;
    }

    public Sinformation getSinformation(){
        return student;
    } //获得学生个人信息
    public void setSinformation(Sinformation student){
        this.student = student;
    }


    //public BmobRelation getStudents(){
      //  return students;
    //}
    //public void setStudents(BmobRelation students){
      //  this.students = students;
    //}

    public String getBlueToothAddres(){
        return BlueToothAddres;
    } //获得蓝牙地址
    public void setBlueToothAddres(String BlueToothAddres){ this.BlueToothAddres = BlueToothAddres; }

    public String getCoursecode(){
        return coursecode;
    } //获得课程代码
    public void setCoursecode(String coursecode){
        this.coursecode = coursecode;
    }

    public String getCoursenum(){
        return coursenum;
    } //获得教务代码
    public void setCoursenum(String coursenum){
        this.coursecode = coursenum;
    }

    public String getCourseterm() {
        return courseterm;
    } //获得开课学期
    public void setCourseterm(String courseterm) {
        this.courseterm = courseterm;
    }

    public String getCoursename() {
        return coursename;
    } //获得课程名称
    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

}
