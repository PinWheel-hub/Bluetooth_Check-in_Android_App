from flask import Blueprint, request, session, g, Response
from flask_restful import Api, Resource, fields, marshal_with, reqparse, marshal, abort
from models import *
from datetime import datetime
import re

tail_dot_rgx = re.compile(r'(?:(\.)|(\.\d*?[1-9]\d*?))0+(?=\b|[^0-9])')

def remove_tail_dot_zeros(a):
    return tail_dot_rgx.sub(r'\2',a)

student_bp = Blueprint('Student', __name__)
student_api = Api(student_bp)

student_curriculum_fields = {
    'Cid': fields.Integer(attribute='Cid'),
    'Cnum': fields.Integer(attribute='Cnum'),
    'Cname': fields.String(attribute='Cname'),
    'Cterm': fields.String(attribute='Cterm'),
    'Tid': fields.Integer(attribute='Tid')
}

# 需要进行权限验证的路由
required_login_list = ['/student_restful/', '/Student/alterinformation/', '/student_curriculum/',
                       '/student_attendance/', '/Student/get_current_attendence/', '/Student/get_attendance_list/']

# 判断是否已经登录
@student_bp.before_app_request
def before_app_request():
    if request.path in required_login_list:  # 判断该路径是否需要权限验证
        if 'sid' in session:
            Sid = session['sid']
            g.student = Student.query.get(Sid)
        else:
            return {'msg_code': 7}  # 未登录

# 学生登录
@student_bp.route('/Student/login/',methods=['POST'])
def student_login():
    Stel = request.form.get('Stel')
    Spwd = request.form.get('Spwd')
    student = Student.query.filter_by(Stel=Stel).first()
    if student:
        if student.Spwd == Spwd:
            session['sid'] = student.Sid
            return {'msg_code': 1}  # 登录成功
        return {'msg_code': 2}  # 用户名或密码错误
    return {'msg_code': 3}  # 用户名不存在

# 学生注册
@student_bp.route('/Student/register/',methods=['POST'])
def student_register():
    Stel = request.form.get('Stel')
    Spwd = request.form.get('Spwd')
    Sname = request.form.get('Sname')
    Snum = request.form.get('Snum')
    Sschool = request.form.get('Sschool')
    if len(Stel) != 11 or (not Stel.isdigit()):
        return {'msg_code': 2}  # 手机号不正确
    student = Student.query.filter_by(Stel=Stel).first()
    if student:
        return {'msg_code': 3}  # 手机号已注册
    student = Student.query.filter_by(Snum=int(Snum), Sschool=Sschool).first()
    if student:
        return {'msg_code': 4}  # 学生已注册
    if(Snum and Sname and Spwd and Stel and Sschool):
        newStudent = Student(Snum, Sname, Spwd, Stel, Sschool)
        db.session.add(newStudent)
        db.session.commit()  # 写入数据库中
        return {'msg_code': 1}  # 注册成功
    return {'msg_code': 5}  # 存在空项
# 学生修改信息
@student_bp.route('/Student/alterinformation/',methods=['POST'])
def student_alterinformation():
    new_Spwd = request.form.get('new_Spwd')
    new_Sname = request.form.get('new_Sname')
    new_Snum = request.form.get('new_Snum')
    new_Sschool = request.form.get('new_Sschool')
    student = Student.query.get(g.student.Sid)
    if student and new_Spwd and new_Sname and new_Snum and new_Sschool:
        student.Spwd = new_Spwd
        student.Sname = new_Sname
        student.Snum = new_Snum
        student.Sschool = new_Sschool
        db.session.commit()
        return {'msg_code': 1}  # 修改成功
    return {'msg_code': 2}  # 修改失败

# 学生选课相关
class student_curriculum(Resource):
    # 获取学生的所有选课
    def get(self):
        Sid = session['sid']
        student = Student.query.filter_by(Sid=Sid).first()
        curriculums = student.CurriculumList
        if curriculums:
            return_msg = []
            for c in curriculums:
                teacher = Teacher.query.filter_by(Tid=c.Tid).first()
                return_msg.append({'Cid': c.Cid,
                                   'Cnum': c.Cnum,
                                   'Cname': c.Cname,
                                   'Cterm': c.Cterm,
                                   'Tname': teacher.Tname})
            return {'msg_code': 1, 'curriculums': return_msg}  # 查询成功
        else:
            return {'msg_code': 2}  # 未查询到课程

    # 学生增加选课
    def post(self):
        Sid = session['sid']
        Cid = request.form.get('Cid')
        curriculum = Curriculum.query.filter_by(Cid=Cid).first()
        if not curriculum:
            return {'msg_code': 2}  # 课程不存在
        student = Student.query.filter_by(Sid=Sid).first()
        curriculums = student.CurriculumList
        for c in curriculums:
            if c.Cid == int(Cid):
                return {'msg_code': 3}  # 重复选课
        newStudent_Curriculum = Student_Curriculum(Sid, Cid)
        db.session.add(newStudent_Curriculum)
        db.session.commit()
        return {'msg_code': 1}  # 增加成功

    def delete(self):
        Sid = session['sid']
        Cid = request.form.get('Cid')
        student_curriculum = Student_Curriculum.query.filter_by(Cid=Cid, Sid=Sid).first()
        if not student_curriculum:
            return {'msg_code': 2}  # 未选该课程
        db.session.delete(student_curriculum)
        db.session.commit()
        return {'msg_code': 1}  # 删除成功

# 学生签到相关
class student_attendance(Resource):
    def get(self):
        Sid = session['sid']
        Cid = request.form.get('Cid')
        attendances = Attendance.query.filter(Attendance.Cid == Cid).all()
        student_attendances = []
        for a in attendances:
            if Student_Attendance.query.filter_by(Sid=Sid, Aid=a.Aid).first():
                student_attendances.append(Student_Attendance.query.filter_by(Sid=Sid, Aid=a.Aid).first())
        if student_attendances:
            return_msg = []
            for sa in student_attendances:
                attendance = Attendance.query.filter_by(Aid=sa.Aid).first()
                return_msg.append({'Atime': attendance.Atime, 'Datetime': str(sa.Datetime)})
            return {'msg_code': 1, 'student_attendances': return_msg}  # 查询成功
        else:
            return {'msg_code': 2}  # 未查询到签到记录

    def post(self):
        Sid = session['sid']
        Cid = request.form.get('Cid')
        isSuccess = request.form.get('isSuccess')
        curriculum = Curriculum.query.filter_by(Cid=Cid).first()
        if not curriculum:
            return {'msg_code': 0}  # 课程已不存在
        Atime = curriculum.Catime
        student_curriculum = Student_Curriculum.query.filter_by(Sid=Sid, Cid=Cid).first()
        if not student_curriculum:
            return {'msg_code': 2}  # 未选课
        attendance = Attendance.query.filter_by(Cid=Cid, Atime=Atime).first()
        if not attendance:
            return {'msg_code': 3}  # 未发起签到
        student_attendance = Student_Attendance.query.filter_by(Sid=Sid, Aid=attendance.Aid).first()
        if student_attendance:
            return {'msg_code': 4}  # 已成功签到
        tdatetime = attendance.ADatetime
        sdatetime = datetime.strptime(datetime.now().strftime('%Y-%m-%d %H:%M:%S'), '%Y-%m-%d %H:%M:%S')
        if (int((sdatetime - tdatetime).seconds) > 300):
            return {'msg_code': 6}  # 签到过期
        if not isSuccess or int(isSuccess) == 0:
            return {'msg_code': 5}  # 签到失败
        newStudent_Attendance = Student_Attendance(Sid, attendance.Aid)
        db.session.add(newStudent_Attendance)
        db.session.commit()
        return {'msg_code': 1}  # 签到成功

class student_restful(Resource):
    # 查询某个学生
    def get(self):
        Sid = session['sid']
        student = Student.query.filter_by(Sid=Sid).first()
        if student:
            return_msg = {
                'msg_code': 1,
                'Spwd': student.Spwd,
                'Snum': student.Snum,
                'Sname': student.Sname,
                'Stel': student.Stel,
                'Sschool': student.Sschool
            }
            return return_msg
        else:
            return {'msg_code': 2}  # 查询的学生不存在

# 学生获取考勤信息
@student_bp.route('/Student/get_current_attendence/',methods=['POST', 'GET'])
def student_current_attendence():
    Sid = session['sid']
    Cid = request.form.get('Cid')
    curriculum = Curriculum.query.filter_by(Cid=Cid).first()
    if not curriculum:
        return {'msg_code': 2}  # 课程已不存在
    Atime = curriculum.Catime
    attendance = Attendance.query.filter_by(Cid=Cid, Atime=Atime).first()
    if not attendance:
        return {'msg_code': 3}  # 未发起签到
    student_attendance = Student_Attendance.query.filter_by(Aid=attendance.Aid, Sid=Sid).first()
    if student_attendance:
        return {'msg_code': 1, 'Atime': attendance.Atime, 'Datetime': str(student_attendance.Datetime)}  # 用户名或密码错误
    return {'msg_code': 4}  # 还未进行签到

@student_bp.route('/Student/get_attendance_list/',methods=['POST', 'GET'])
def student_get_attendance_list():
    Sid = session['sid']
    Cid = request.form.get('Cid')
    curriculum = Curriculum.query.filter_by(Cid=Cid).first()
    if not curriculum:
        return {'msg_code': 2}  # 未查询到签到记录
    attendances = Attendance.query.filter(Attendance.Cid == Cid).all()
    student_attendances = []
    Satime = 0
    for a in attendances:
        if Student_Attendance.query.filter_by(Sid=Sid, Aid=a.Aid).first():
            student_attendances.append(Student_Attendance.query.filter_by(Sid=Sid, Aid=a.Aid).first())
    if student_attendances:
        return_msg = []
        for sa in student_attendances:
            attendance = Attendance.query.filter_by(Aid=sa.Aid).first()
            return_msg.append({'Atime': attendance.Atime, 'Datetime': str(sa.Datetime)})
            Satime += 1
        return {'msg_code': 1,
                'Mark': remove_tail_dot_zeros(str("%.2f" % (100 * Satime / curriculum.Catime))),
                'student_attendances': return_msg}  # 查询成功
    else:
        if curriculum.Catime > 0:
            return {'msg_code': 3, 'Mark': '0'}  # 未查询到签到记录
        else:
            return {'msg_code': 4, 'Mark': '100 (当前还未发起过签到)'}  # 未发起过签到

student_api.add_resource(student_curriculum, '/student_curriculum/')
student_api.add_resource(student_restful, '/student_restful/')
student_api.add_resource(student_attendance, '/student_attendance/')