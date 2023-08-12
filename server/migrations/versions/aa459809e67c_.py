"""empty message

Revision ID: aa459809e67c
Revises: cee42a3daab1
Create Date: 2021-11-16 16:32:24.110224

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'aa459809e67c'
down_revision = 'cee42a3daab1'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('curriculum', sa.Column('Bluetooth', sa.String(length=20), nullable=True))
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('curriculum', 'Bluetooth')
    # ### end Alembic commands ###
