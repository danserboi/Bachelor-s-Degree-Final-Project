from flask import jsonify


class CustomHTTPException(Exception):

    def __init__(self, message, status_code):
        super().__init__()
        self.message = message
        self.status_code = status_code

    def get(self):
        return {'message': self.message}


class BadRequestException(CustomHTTPException):

    def __init__(self, message='Bad request.'):
        super().__init__(message=message, status_code=400)


class UnauthorizedException(CustomHTTPException):

    def __init__(self, message='Not authorized.'):
        super().__init__(message=message, status_code=401)


class ForbiddenException(CustomHTTPException):

    def __init__(self, message='Forbidden.'):
        super().__init__(message=message, status_code=403)


class NotFoundException(CustomHTTPException):

    def __init__(self, message='Not Found.'):
        super().__init__(message=message, status_code=404)


class ConflictException(CustomHTTPException):

    def __init__(self, message='Conflict.'):
        super().__init__(message=message, status_code=409)


class InternalServerErrorException(CustomHTTPException):

    def __init__(self, message="Something went wrong."):
        super().__init__(message=message, status_code=500)


def handle_exception(error: CustomHTTPException):
    response = jsonify(error.get())
    response.status_code = error.status_code
    return response


def handle_general_exception(_):
    return handle_exception(InternalServerErrorException())
