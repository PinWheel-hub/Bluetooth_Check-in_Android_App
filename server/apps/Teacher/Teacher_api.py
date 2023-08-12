from flask import Blueprint, request, session, g
from flask_restful import Api, Resource, fields, marshal_with, reqparse, marshal, abort
from models import *
from datetime import datetime
import re

tail_dot_rgx = re.compile(r'(?:(\.)|(\.\d*?[1-9]\d*?))0+(?=\b|[^0-9])')

def remove_tail_dot_zeros(a):
    return tail_dot_rgx.sub(r'\2',a)


teacher_bp = Blueprint('Teacher', __name__)
teacher_api = Api(teacher_bp)

teacher_curriculum_fields = {
    'Cid': fields.Integer(attribute='Cid'),
    'Cnum': fields.String(attribute='Cnum'),
    'Cname': fields.String(attribute='Cname'),
    'Cterm': fields.String(attribute='Cterm')
}

teacher_attendance_fields = {
    'Atime': fields.String(attribute='Atime'),
    'ADatetime': fields.String(attribute='ADatetime')
}

# 需要进行权限验证的路由
required_login_list = ['/teacher_restful/', '/Teacher/alterinformation/', '/teacher_curriculum/', '/teacher_attendance/'
    , '/attendance_situation/', '/Teacher/get_student_attendance/', '/Teacher/get_curriculum_students/']

# 判断是否已经登录
@teacher_bp.before_app_request
def before_app_request():
    if request.path in required_login_list:  # 判断该路径是否需要权限验证
        if 'tid' in session:
            Tid = session['tid']
            g.teacher = Teacher.query.get(Tid)  # 验证成功
        else:
            return {'msg_code': 7}  # 未登录

# 教师登录
@teacher_bp.route('/Teacher/login/',methods=['POST'])
def teacher_login():
    Ttel = request.form.get('Ttel')
    Tpwd = request.form.get('Tpwd')
    teacher = Teacher.query.filter_by(Ttel=Ttel).first()
    if teacher:
        if teacher.Tpwd == Tpwd:
            session['tid'] = teacher.Tid
            return {'msg_code': 1}  # 登录成功
        return {'msg_code': 2}  # 用户名或密码错误
    return {'msg_code': 3}  # 用户名不存在

# 教师注册
@teacher_bp.route('/Teacher/register/',methods=['POST'])
def teacher_register():
    Ttel = request.form.get('Ttel')
    Tpwd = request.form.get('Tpwd')
    Tname = request.form.get('Tname')
    Tnum = request.form.get('Tnum')
    Tschool = request.form.get('Tschool')
    if len(Ttel) != 11 or (not Ttel.isdigit()):
        return {'msg_code': 2}  # 手机号不正确
    teacher = Teacher.query.filter_by(Ttel=Ttel).first()
    if teacher:
        return {'msg_code': 3}  # 手机号已注册
    teacher = Teacher.query.filter_by(Tnum=int(Tnum), Tschool=Tschool).first()
    if teacher:
        return {'msg_code': 4}  # 教师已注册
    if(Tnum and Tname and Tpwd and Ttel and Tschool):
        newTeacher = Teacher(Tnum, Tname, Tpwd, Ttel, Tschool)
        db.session.add(newTeacher)
        db.session.commit()  # 写入数据库中
        return {'msg_code': 1} # 注册成功
    return {'msg_code': 5} # 存在空项

# 教师修改信息
@teacher_bp.route('/Teacher/alterinformation/',methods=['POST'])
def teacher_alterinformation():
    new_Tpwd = request.form.get('new_Tpwd')
    new_Tname = request.form.get('new_Tname')
    new_Tnum = request.form.get('new_Tnum')
    new_Tschool = request.form.get('new_Tschool')
    teacher = Teacher.query.get(g.teacher.Tid)
    if teacher and new_Tpwd and new_Tname and new_Tnum and new_Tschool:
        teacher.Tpwd = new_Tpwd
        teacher.Tname = new_Tname
        teacher.Tnum = new_Tnum
        teacher.Tschool = new_Tschool
        db.session.commit()
        return {'msg_code': 1}  # 修改成功
    return {'msg_code': 2}  # 修改失败

class teacher_curriculum(Resource):
    # 获取教师的所有课程
    def get(self):
        Tid = session['tid']
        curriculums = Curriculum.query.filter(Curriculum.Tid == Tid).all()
        if curriculums:
            return {'msg_code': 1, 'curriculums': marshal(curriculums, teacher_curriculum_fields)}  # 查询成功
        else:
            return {'msg_code': 2}  # 未查询到课程

    # 教师增加课程
    def post(self):
        Tid = session['tid']
        Cnum = request.form.get('Cnum')
        Cname = request.form.get('Cname')
        Cterm = request.form.get('Cterm')
        Bluetooth = request.form.get('Bluetooth')
        curriculums = Curriculum.query.filter(Curriculum.Tid == Tid).all()
        for c in curriculums:
            if c.Cnum == Cnum:
                return {'msg_code': 2}  # 重复创建课程
        if(Cnum and Cname and Cterm and Bluetooth):
            newCurriculum = Curriculum(Cnum, Cname, Cterm, Tid, Bluetooth)
            db.session.add(newCurriculum)
            db.session.commit()
            return {'msg_code': 1, 'Cid': newCurriculum.Cid}  # 增加成功
        return {'msg_code': 3}  # 存在空项

    def put(self):
        Tid = session['tid']
        Cid = request.form.get('Cid')
        Cnum = request.form.get('Cnum')
        Cname = request.form.get('Cname')
        Cterm = request.form.get('Cterm')
        Bluetooth = request.form.get('Bluetooth')
        curriculum = Curriculum.query.filter_by(Cid=Cid).first()
        if(not curriculum):
            return {'msg_code': 2}  # 课程已不存在
        if(Cnum and Cname and Cterm and Bluetooth):
            curriculums = Curriculum.query.filter(Curriculum.Tid == Tid).all()
            for c in curriculums:
                if c.Cnum == Cnum and not str(c.Cid) == Cid:
                    return{'msg_code': 3}  # 教务代码重复
            curriculum.Cnum = Cnum
            curriculum.Cname = Cname
            curriculum.Cterm = Cterm
            curriculum.Bluetooth = Bluetooth
            db.session.commit()
            return {'msg_code': 1}  # 增加成功
        return {'msg_code': 4}  # 存在空项

    # 教师删除课程
    def delete(self):
        Tid = session['tid']
        Cid = request.form.get('Cid')
        curriculum = Curriculum.query.filter_by(Tid=Tid, Cid=Cid).first()
        if curriculum:
            student_curriculums = Student_Curriculum.query.filter(Student_Curriculum.Cid == Cid).all()
            for sc in student_curriculums:
                db.session.delete(sc)
                db.session.commit()
            attendances = Attendance.query.filter(Attendance.Cid == Cid).all()
            for a in attendances:
                student_attendances = Student_Attendance.query.filter(Student_Attendance.Aid == a.Aid).all()
                for sa in student_attendances:
                    db.session.delete(sa)
                    db.session.commit()
                db.session.delete(a)
                db.session.commit()
            for a in attendances:
                db.session.delete(a)
                db.session.commit()
            db.session.delete(curriculum)
            db.session.commit()
            return {'msg_code': 1}  # 删除成功
        return {'msg_code': 2}  # 课程不存在

class teacher_attendance(Resource):
    # 查询签到情况
    def get(self):
        Tid = session['tid']
        Cid = request.form.get('Cid')
        attendances = Attendance.query.filter(Attendance.Cid == Cid, Attendance.Tid == Tid).all()
        if attendances:
            return {'msg_code': 1, 'teacher_attendances': marshal(attendances, teacher_attendance_fields)}  # 查询成功
        else:
            return {'msg_code': 2}  # 未发起过签到

    # 发起签到
    def post(self):
        Tid = session['tid']
        Cid = request.form.get('Cid')
        curriculum = Curriculum.query.filter_by(Cid=Cid, Tid=Tid).first()
        if not curriculum:
            return {'msg_code': 2}  # 课程不存在
        if curriculum.Catime > 0:
            last_attendance = Attendance.query.filter_by(Cid=Cid, Atime=curriculum.Catime).first()
            if not last_attendance:
                return {'msg_code': 3}  # 考勤记录出错
            last_datetime = last_attendance.ADatetime
            current_datetime = datetime.strptime(datetime.now().strftime('%Y-%m-%d %H:%M:%S'), '%Y-%m-%d %H:%M:%S')
            if(int((current_datetime - last_datetime).seconds) < 300):
                return {'msg_code': 4}  # 当前正在发起签到
        Atime = curriculum.Catime + 1
        curriculum.Catime += 1
        newAttendance = Attendance(Cid, Tid, Atime)
        db.session.add(newAttendance)
        db.session.commit()
        return {'msg_code': 1, 'Atime': Atime}  # 发起成功

class attendance_situation(Resource):
    def post(self):  # get方法
        Tid = session['tid']
        Cid = request.form.get('Cid')
        curriculum = Curriculum.query.filter_by(Cid=Cid, Tid=Tid).first()
        if (not curriculum):
            return {'msg_code': 2}  # 课程不存在
        students = curriculum.StudentList
        Atime = curriculum.Catime
        attendance = Attendance.query.filter_by(Tid=Tid, Cid=Cid, Atime=Atime).first()
        if (not attendance):
            return {'msg_code': 3}  # 未发起签到
        signed_students = attendance.Studentlist
        if students:
            return_msg1 = []
            return_msg2 = []
            for s in students:
                if_signed = 0
                for ss in signed_students:
                    if(s.Sid == ss.Sid):
                        return_msg1.append({'Sid': s.Sid, 'Snum': s.Snum, 'Sname': s.Sname})
                        if_signed = 1
                        break
                if(if_signed == 0):
                    return_msg2.append({'Sid': s.Sid, 'Snum': s.Snum, 'Sname': s.Sname})
            return {'msg_code': 1,
                    'Atime': attendance.Atime,
                    'signed_students': return_msg1,
                    'unsigned_students': return_msg2}  # 查询成功
        else:
            return {'msg_code': 4, 'Atime': attendance.Atime}  # 未有学生选课

class teacher_restful(Resource):
    # 查询某个教师
    def get(self):
        Tid = session['tid']
        teacher = Teacher.query.filter_by(Tid=Tid).first()
        if teacher:
            return_msg = {
                'msg_code': 1,
                'Tpwd': teacher.Tpwd,
                'Tnum': teacher.Tnum,
                'Tname': teacher.Tname,
                'Ttel': teacher.Ttel,
                'Tschool': teacher.Tschool
            }
            return return_msg
        else:
            return {'msg_code': 2}  # 查询的教师不存在

@teacher_bp.route('/Teacher/get_student_attendance/',methods=['POST', 'GET'])
def teacher_get_student_attendance():
    Sid = request.form.get('Sid')
    Cid = request.form.get('Cid')
    curriculum = Curriculum.query.filter_by(Cid=Cid).first()
    if not curriculum:
        return {'msg_code': 2}  # 未查询到签到记录
    attendances = Attendance.query.filter(Attendance.Cid == Cid).all()
    student_attendances = []
    for a in attendances:
        if Student_Attendance.query.filter_by(Sid=Sid, Aid=a.Aid).first():
            student_attendances.append(Student_Attendance.query.filter_by(Sid=Sid, Aid=a.Aid).first())
    Satime = 0
    if student_attendances:
        return_msg = []
        for sa in student_attendances:
            attendance = Attendance.query.filter_by(Aid=sa.Aid).first()
            return_msg.append({'Atime': attendance.Atime, 'Datetime': str(sa.Datetime)})
            Satime += 1
        return {'msg_code': 1,
                'Mark': remove_tail_dot_zeros(str("%.2f" % (100*Satime/curriculum.Catime))),
                'student_attendances': return_msg}  # 查询成功
    else:
        if curriculum.Catime > 0:
            return {'msg_code': 3, 'Mark': '0'}  # 未查询到签到记录
        else:
            return {'msg_code': 4, 'Mark': '100 (当前还未发起过签到)'}  # 未发起过签到

@teacher_bp.route('/Teacher/get_curriculum_students/',methods=['POST', 'GET'])
    # 查询某个课程学生
def get_curriculum_students():
    Cid = request.form.get('Cid')
    curriculum = Curriculum.query.filter_by(Cid=Cid).first()
    if not curriculum:
        return {'msg_code': 2}  # 课程已不存在
    students = curriculum.StudentList
    if students:
        return_msg = []
        for s in students:
            Satime = 0
            if curriculum.Catime > 0:
                attendances = curriculum.Attendancelist
                for a in attendances:
                    sa = Student_Attendance.query.filter_by(Sid=s.Sid, Aid=a.Aid).first()
                    if(sa):
                        Satime += 1
                return_msg.append({'Sid': s.Sid,
                                   'Snum': s.Snum,
                                   'Sname': s.Sname,
                                   'Mark': remove_tail_dot_zeros(str("%.2f" % (100*Satime/curriculum.Catime)))})
            else:
                return_msg.append({'Sid': s.Sid,
                                   'Snum': s.Snum,
                                   'Sname': s.Sname,
                                   'Mark': '100 (当前还未发起过签到)'})
        return {'msg_code': 1, 'students': return_msg}  # 查询成功
    else:
        return {'msg_code': 3}  # 无学生选课

teacher_api.add_resource(teacher_curriculum, '/teacher_curriculum/')
teacher_api.add_resource(teacher_attendance, '/teacher_attendance/')
teacher_api.add_resource(teacher_restful, '/teacher_restful/')
teacher_api.add_resource(attendance_situation, '/attendance_situation/')
