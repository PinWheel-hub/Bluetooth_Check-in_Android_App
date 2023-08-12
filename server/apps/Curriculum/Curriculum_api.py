from flask import Blueprint, request, session, g
from flask_restful import Api, Resource, fields, marshal_with, reqparse, marshal, abort
from models import *

curriculum_bp = Blueprint('Curriculum', __name__)
curriculum_api = Api(curriculum_bp)

@curriculum_bp.route('/get_curriculum/',methods=['POST', 'GET'])
    # 查询某个课程
def get_curriculum():
    Cid = request.form.get('Cid')
    curriculum = Curriculum.query.filter_by(Cid=Cid).first()
    if curriculum:
        teacher = Teacher.query.filter_by(Tid=curriculum.Tid).first()
        return_msg = {
            'msg_code': 1,
            'Cid': curriculum.Cid,
            'Cnum': curriculum.Cnum,
            'Cname': curriculum.Cname,
            'Cterm': curriculum.Cterm,
            'Bluetooth': curriculum.Bluetooth,
            'Tname': teacher.Tname
        }
        return return_msg
    else:
        return {'msg_code': 2}  # 查询的课程不存在

