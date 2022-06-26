from datetime import timedelta

import jwt
from flask import Blueprint, request, current_app, Response
from flask_accept import accept

from src.api.decorators import auth_token_required
from src.api.exceptions import BadRequestException, NotFoundException, UnauthorizedException, \
    ForbiddenException
from src.extensions import bcrypt, db
from src.models.token_blacklist import BlacklistedToken
from src.models.user import User
from src.utils.datetime_utils import utc_now, local_utc_offset
from src.utils.db_access import session_scope

users_blueprint = Blueprint('users', __name__)


@users_blueprint.route('/register', methods=['POST'])
@accept('application/json')
def register_user():
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    email = json_data.get('email')
    password = json_data.get('password')
    first_name = json_data.get('first_name')
    last_name = json_data.get('last_name')
    if not email or not password or not first_name or not last_name:
        raise BadRequestException()

    user = User.first_by(email=email)
    if not user:
        new_user = User(email=email, password=password, first_name=first_name, last_name=last_name)
        with session_scope():
            db.session.add(new_user)

        with session_scope():
            expire_hours = current_app.config.get("EMAIL_TOKEN_EXPIRE_HOURS")
            expire_minutes = current_app.config.get("EMAIL_TOKEN_EXPIRE_MINUTES")
            token = new_user.encode_token(expire_hours, expire_minutes, new_user.id)
            new_user.email_validation_token_hash = bcrypt.generate_password_hash(token, current_app.config.get(
                'BCRYPT_LOG_ROUNDS')).decode()

        from src.api.users.business import send_welcome_email
        send_welcome_email(new_user, token)

        expire_hours = current_app.config.get("TOKEN_EXPIRE_HOURS")
        expire_minutes = current_app.config.get("TOKEN_EXPIRE_MINUTES")
        auth_token = new_user.encode_token(expire_hours, expire_minutes, new_user.id)
        return {
                   'auth_token': auth_token
               }, 201
    else:
        raise BadRequestException(message='Email is already taken.')


@users_blueprint.route('/login', methods=['POST'])
@accept('application/json')
def login_user():
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    email = json_data.get('email')
    password = json_data.get('password')
    if not email or not password:
        raise BadRequestException()

    user = User.first_by(email=email)
    if user:
        try:
            if not (bcrypt.check_password_hash(user.password, password)):
                raise UnauthorizedException(message="Incorrect password.")
        except:
            raise UnauthorizedException(message="Incorrect password.")
        expire_hours = current_app.config.get("TOKEN_EXPIRE_HOURS")
        expire_minutes = current_app.config.get("TOKEN_EXPIRE_MINUTES")
        auth_token = user.encode_token(expire_hours, expire_minutes, user.id)
        return {
                   'auth_token': auth_token
               }, 200
    else:
        raise NotFoundException(message='No user with this email address.')


@users_blueprint.route('/logout', methods=['POST'])
@accept('application/json')
def logout_user():
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        raise UnauthorizedException("Already logged out.")
    auth_token = auth_header.split(" ")[1]
    user_id = User.decode_token(auth_token)
    user = User.get(user_id)
    if not user:
        raise UnauthorizedException(message='Invalid user.')
    payload = jwt.decode(auth_token, current_app.config.get('SECRET_KEY'), algorithms=["HS256"])
    blacklisted_token = BlacklistedToken(auth_token, payload["exp"])
    with session_scope():
        db.session.add(blacklisted_token)
    return Response(status=200)


@users_blueprint.route('/profile', methods=['GET'])
@accept('application/json')
@auth_token_required
def get_user_status(user_id: int):
    user = User.get(user_id)
    return user.serialize, 200


@users_blueprint.route('/profile', methods=['PUT'])
@accept('application/json')
@auth_token_required
def update_profile(user_id: int):
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    first_name = json_data.get('first_name')
    last_name = json_data.get('last_name')
    if not first_name and not last_name:
        raise BadRequestException()

    user = User.get(user_id)
    if not user:
        raise UnauthorizedException(message='Invalid user.')

    with session_scope():
        if first_name:
            user.first_name = first_name
        if last_name:
            user.last_name = last_name

    return user.serialize, 200


@users_blueprint.route('/email_validation', methods=['GET'])
@auth_token_required
def email_validation(user_id):
    user = User.get(user_id)
    if user.email_validation_date:
        raise BadRequestException("Your email address has been already validated.")
    expire_hours = current_app.config.get("EMAIL_TOKEN_EXPIRE_HOURS")
    expire_minutes = current_app.config.get("EMAIL_TOKEN_EXPIRE_MINUTES")
    token = user.encode_token(expire_hours, expire_minutes, user.id)
    with session_scope():
        user.email_validation_token_hash = bcrypt.generate_password_hash(token,
                                                                         current_app.config.get(
                                                                             'BCRYPT_LOG_ROUNDS')).decode()
    from src.api.users.business import send_email_validation_email
    send_email_validation_email(user, token)
    return Response(status=200)


@users_blueprint.route('/email_validation/<token>', methods=['GET'])
def confirm_email(token):
    user_id = User.decode_token(token)
    user = User.get(user_id)
    if not user or not user.email_validation_token_hash:
        raise NotFoundException(message='Token is not valid.')
    try:
        if not (bcrypt.check_password_hash(user.email_validation_token_hash, token)):
            raise UnauthorizedException(message="Incorrect token.")
    except:
        raise UnauthorizedException(message="Incorrect token.")

    with session_scope():
        user.email_validation_token_hash = None
        user.email_validation_date = utc_now()
    return Response(status=200)


@users_blueprint.route('/password_change', methods=['PUT'])
@accept('application/json')
@auth_token_required
def password_change(user_id: int):
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    old_password = json_data.get('old_password')
    new_password = json_data.get('new_password')
    if not old_password or not new_password:
        raise BadRequestException()

    user = User.get(user_id)
    try:
        if not bcrypt.check_password_hash(user.password, old_password):
            raise BadRequestException(message='Incorrect password.')
    except:
        raise BadRequestException(message='Incorrect password.')

    with session_scope():
        user.password = bcrypt.generate_password_hash(new_password,
                                                      current_app.config.get('BCRYPT_LOG_ROUNDS')).decode()
    return Response(status=200)


@users_blueprint.route('/forgot_password', methods=['POST'])
@accept('application/json')
def forgot_password():
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    email = json_data.get('email')
    if not email:
        raise BadRequestException()

    user = User.first_by(email=email)
    if user:
        from src.utils.reset_pin import generate_reset_pin
        reset_pin = generate_reset_pin()

        with session_scope():
            user.reset_pin_hash = bcrypt.generate_password_hash(reset_pin,
                                                                current_app.config.get('BCRYPT_LOG_ROUNDS')).decode()
            user.reset_pin_expiration_date = utc_now() + timedelta(
                minutes=current_app.config.get('RESET_PIN_EXPIRE_MINUTES'))

        from src.api.users.business import send_password_recovery_email
        send_password_recovery_email(user, reset_pin)
        return Response(status=200)
    else:
        raise NotFoundException(message='No user with this email address.')


@users_blueprint.route('/account_recovery', methods=['PUT'])
@accept('application/json')
def account_recovery():
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    reset_pin = json_data.get('reset_pin')
    email = json_data.get('email')
    new_password = json_data.get('new_password')
    if not reset_pin or not email or not new_password:
        raise BadRequestException()

    user = User.first_by(email=email)
    if user:
        try:
            if not (bcrypt.check_password_hash(user.reset_pin_hash, reset_pin)):
                raise UnauthorizedException(message="Reset pin is not valid.")
        except:
            raise UnauthorizedException(message="Reset pin is not valid.")
        if user.reset_pin_expiration_date.replace(tzinfo=local_utc_offset()) < utc_now():
            raise UnauthorizedException(message='Reset pin expired.')
        with session_scope():
            user.reset_pin_hash = None
            user.reset_pin_expiration_date = None
            user.password = bcrypt.generate_password_hash(new_password,
                                                          current_app.config.get('BCRYPT_LOG_ROUNDS')).decode()
        return Response(status=200)
    else:
        raise NotFoundException(message='No user with this email address.')


@users_blueprint.route('/users/<id>', methods=['GET'])
@accept('application/json')
@auth_token_required
def get_user(user_id, id):
    user = User.get(user_id)
    if user.admin:
        try:
            user = User.get(int(id))
            if not user:
                raise NotFoundException(message='No user with this id.')
            return user.serialize, 200
        except ValueError:
            raise NotFoundException(message='No user with this id.')
    else:
        raise ForbiddenException()
