import datetime
from src.utils.datetime_utils import utc_now
import json
import time

from flask import current_app

from src import User
from src.extensions import bcrypt, db
from src.utils.db_access import session_scope
from tests.parent_test_case import ParentTestCase


class TestUsersBlueprint(ParentTestCase):
    # registration tests
    def test_register(self):
        with self.client:
            resp = self.client.post(
                '/register',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt',
                    first_name='Ibrahim',
                    last_name='Trello'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 201)
            self.assertEqual(resp.content_type, 'application/json')
            self.assertTrue(data['auth_token'])

    def test_duplicate_register(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            resp = self.client.post(
                '/register',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt',
                    first_name='Ibrahim',
                    last_name='Trello'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 400)
            self.assertEqual(resp.content_type, 'application/json')
            self.assertEqual(data['message'], 'Email is already taken.')

    def test_invalid_register(self):
        with self.client:
            resp = self.client.post(
                '/register',
                data=json.dumps(dict()),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 400)
            self.assertEqual(data['message'], 'Bad request.')

    def test_register_missing_field(self):
        with self.client:
            resp = self.client.post(
                '/register',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt',
                    first_name='Ibrahim',
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 400)
            self.assertEqual(data['message'], 'Bad request.')

    # Login tests
    def test_login(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 200)
            self.assertEqual(resp.content_type, 'application/json')
            self.assertTrue(data['auth_token'])

    def test_not_registered_user_login(self):
        with self.client:
            resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 404)
            self.assertEqual(resp.content_type, 'application/json')
            self.assertEqual(data['message'], 'No user with this email address.')

    # logout tests
    def test_logout(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(login_resp.data.decode())
            self.assertEqual(login_resp.status_code, 200)
            self.assertEqual(login_resp.content_type, 'application/json')
            self.assertTrue(data['auth_token'])
            resp = self.client.post(
                '/logout',
                headers=[('Accept', 'application/json'),
                         ('Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])]
            )
            self.assertEqual(resp.status_code, 200)
            self.assertEqual(login_resp.content_type, 'application/json')

    def test_logout_expired_token(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(login_resp.data.decode())
            self.assertEqual(login_resp.status_code, 200)
            self.assertEqual(login_resp.content_type, 'application/json')
            self.assertTrue(data['auth_token'])
            time.sleep(current_app.config["TOKEN_EXPIRE_SECONDS"] + 1)
            resp = self.client.post(
                '/logout',
                headers=[('Accept', 'application/json'),
                         ('Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 401)
            self.assertEqual(login_resp.content_type, 'application/json')
            self.assertEqual(data['message'], 'Token expired.')

    def test_logout_invalid_token(self):
        with self.client:
            resp = self.client.post(
                '/logout',
                headers=[('Accept', 'application/json'), ('Authorization', 'Bearer 123')])
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 401)
            self.assertEqual(resp.content_type, 'application/json')
            self.assertEqual(data['message'], 'Invalid token.')

    # profile tests
    def test_profile(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(login_resp.data.decode())
            self.assertEqual(login_resp.status_code, 200)
            self.assertEqual(login_resp.content_type, 'application/json')
            self.assertTrue(data['auth_token'])
            resp = self.client.get(
                '/profile',
                headers=[('Accept', 'application/json'),
                         ('Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 200)
            self.assertEqual(resp.content_type, 'application/json')
            self.assertEqual(data['email'], 'ibrahim.trello@gmail.com')
            self.assertEqual(data['first_name'], 'Ibrahim')
            self.assertEqual(data['last_name'], 'Trello')
            self.assertTrue('created_at' in data)
            self.assertTrue('email_validation_date' in data)

    def test_invalid_profile(self):
        with self.client:
            resp = self.client.get(
                '/profile',
                headers=[('Accept', 'application/json'), ('Authorization', 'Bearer 123')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 401)
            self.assertEqual(data['message'], 'Invalid token.')

    # password change tests
    def test_password_change(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(login_resp.data.decode())
            self.assertEqual(login_resp.status_code, 200)
            self.assertEqual(login_resp.content_type, 'application/json')
            self.assertTrue(data['auth_token'])

            resp = self.client.put(
                '/password_change',
                data=json.dumps(dict(
                    old_password='qwe123Rt',
                    new_password='asdTYU543'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json'),
                         ('Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])]
            )
            self.assertEqual(resp.status_code, 200)

            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='asdTYU543'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(login_resp.data.decode())
            self.assertEqual(resp.status_code, 200)
            self.assertTrue(data['auth_token'])

    def test_password_change_wrong_password(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )

            resp = self.client.put(
                '/password_change',
                data=json.dumps(dict(
                    old_password='qwe1234Rt',
                    new_password='asdTYU543'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json'),
                         ('Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 400)
            self.assertEqual(data['message'], 'Incorrect password.')

    # forgot password tests
    def test_forgot_password(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            resp = self.client.post(
                '/forgot_password',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            self.assertEqual(resp.status_code, 200)

    def test_forgot_password_unregistered(self):
        with self.client:
            resp = self.client.post(
                '/forgot_password',
                data=json.dumps(dict(
                    email='he@he.com'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            data = json.loads(resp.data.decode())
            self.assertEqual(resp.status_code, 404)
            self.assertIn(data['message'], 'No user with this email address.')

    # email validation tests
    def test_email_validation(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )

            resp = self.client.get(
                '/email_validation',
                content_type='application/json',
                headers=[('Accept', 'application/json'),
                         ('Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])]
            )
            self.assertEqual(resp.status_code, 200)

    def test_confirm_email(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        with session_scope():
            db.session.add(user)
        expire_hours = current_app.config.get("EMAIL_TOKEN_EXPIRE_HOURS")
        expire_minutes = current_app.config.get("EMAIL_TOKEN_EXPIRE_MINUTES")
        token = user.encode_token(expire_hours, expire_minutes, user.id)
        with session_scope():
            user.email_validation_token_hash = bcrypt.generate_password_hash(token, current_app.config.get('BCRYPT_LOG_ROUNDS')).decode()

        with self.client:
            resp = self.client.get(
                f'/email_validation/{token}',
            )
            self.assertEqual(resp.status_code, 200)
            self.assertIsNotNone(user.email_validation_date)

    def test_single_user(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        user.admin = True
        with session_scope():
            db.session.add(user)
            db.session.add(User(email='1@1.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello'))
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            response = self.client.get(f'/users/{user.id}', headers=[('Accept', 'application/json'), (
            'Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])])
            data = json.loads(response.data.decode())
            self.assertEqual(response.status_code, 200)
            self.assertTrue('created_at' in data)
            self.assertIn('ibrahim.trello@gmail.com', data['email'])

    def test_single_user_invalid_id(self):
        user = User(email='ibrahim.trello@gmail.com', password='qwe123Rt', first_name='Ibrahim', last_name='Trello')
        user.admin = True
        with session_scope():
            db.session.add(user)
        with self.client:
            login_resp = self.client.post(
                '/login',
                data=json.dumps(dict(
                    email='ibrahim.trello@gmail.com',
                    password='qwe123Rt'
                )),
                content_type='application/json',
                headers=[('Accept', 'application/json')]
            )
            response = self.client.get('/users/a', headers=[('Accept', 'application/json'), (
            'Authorization', 'Bearer ' + json.loads(login_resp.data.decode())['auth_token'])])
            data = json.loads(response.data.decode())
            self.assertEqual(response.status_code, 404)
            self.assertIn('No user with this id.', data['message'])
