from functools import wraps

from flask import request

from src.api.exceptions import UnauthorizedException
from src.models.user import User


def auth_token_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        auth_header = request.headers.get('Authorization')
        if not auth_header:
            raise UnauthorizedException()
        auth_token = auth_header.split(" ")[1]
        user_id = User.decode_token(auth_token)
        user = User.get(user_id)
        if not user:
            raise UnauthorizedException()
        return f(user_id, *args, **kwargs)

    return decorated_function
