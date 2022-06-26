from flask_testing import TestCase
from src.utils.db_access import session_scope
from run import app
from src.extensions import db


class ParentTestCase(TestCase):
    def create_app(self):
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        db.create_all()

    def tearDown(self):
        with session_scope():
            db.session.remove()
            db.drop_all()
