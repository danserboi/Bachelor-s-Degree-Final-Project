import re

import jwt
from datetime import timedelta
from flask import current_app
from sqlalchemy.orm import relationship, validates
from email_validator import validate_email

from src.extensions import db, bcrypt
from src.api.exceptions import UnauthorizedException
from src.models.token_blacklist import BlacklistedToken
from src.utils.datetime_utils import utc_now


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)

    admin = db.Column(db.Boolean, default=False)

    email = db.Column(db.String(255), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=True)
    first_name = db.Column(db.String(128))
    last_name = db.Column(db.String(128))

    created_at = db.Column(db.DateTime, nullable=False, default=utc_now)
    email_validation_token_hash = db.Column(db.String(255), nullable=True)
    email_validation_date = db.Column(db.DateTime, nullable=True, default=None)
    reset_pin_hash = db.Column(db.String(255), nullable=True)
    reset_pin_expiration_date = db.Column(db.DateTime, nullable=True)
    updated_at = db.Column(db.DateTime, nullable=False, default=utc_now, onupdate=utc_now)

    quotes = relationship('Quote', back_populates='owner', cascade="all, delete")
    collections = relationship('Collection', back_populates='owner', cascade="all, delete")
    likes = relationship('Like', back_populates='user', cascade="all, delete")
    reviews = relationship('Review', back_populates='user', cascade="all, delete")

    def __init__(self, email: str, password: str, first_name: str, last_name: str, admin: bool = False):
        self.email = email
        if password:
            self.password = bcrypt.generate_password_hash(password,
                                                          current_app.config.get('BCRYPT_LOG_ROUNDS')).decode()
        self.first_name = first_name
        self.last_name = last_name
        self.admin = admin

    def __repr__(self):
        return (
            f"User: email={self.email}, id={self.id}, admin={self.admin}"
        )

    @validates('email')
    def validate_email(self, key, address):
        # validate and also normalize it
        return validate_email(address).email

    @validates('password')
    def validate_email(self, key, pw):
        if re.match("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=\\S+$).{8,255}$", pw):
            return pw
        else:
            raise ValueError("Password needs to be at least 8 characters long and contain one lowercase letter, "
                             "one uppercase letter, and a number.")

    @property
    def serialize(self):
        info_dict = {
            'email': self.email,
            'first_name': self.first_name,
            'last_name': self.last_name,
            'created_at': self.created_at,
            'email_validation_date': self.email_validation_date,
        }

        return info_dict

    @staticmethod
    def get(user_id):
        return User.query.get(user_id)

    @staticmethod
    def first_by(**kwargs):
        return User.query.filter_by(**kwargs).first()

    def encode_token(self, expire_hours, expire_minutes, sub) -> str:
        now = utc_now()
        expire = now + timedelta(hours=expire_hours, minutes=expire_minutes)
        if current_app.testing:
            expire = now + timedelta(seconds=current_app.config["TOKEN_EXPIRE_SECONDS"])
        payload = dict(exp=expire, iat=now, sub=sub)
        key = current_app.config.get("SECRET_KEY")
        return jwt.encode(payload, key, algorithm="HS256")

    @staticmethod
    def decode_token(auth_token: str):
        try:
            payload = jwt.decode(auth_token, current_app.config.get('SECRET_KEY'), algorithms=["HS256"])
            if BlacklistedToken.is_blacklisted(auth_token):
                raise UnauthorizedException(message='Token blacklisted. Please log in again.')
            return payload['sub']
        except jwt.ExpiredSignatureError:
            raise UnauthorizedException(message='Token expired.')
        except jwt.InvalidTokenError:
            raise UnauthorizedException(message='Invalid token.')
