import os
from flask import Flask
import src.api.exceptions
from src.models.user import User


def create_app():
    app = Flask(__name__)
    app.config.from_object(os.getenv('CONFIG'))

    register_extensions(app)
    register_blueprints(app)
    register_err_handlers(app)

    return app


def register_extensions(app):
    from src.extensions import db, migrate, bcrypt, mail
    db.init_app(app)
    bcrypt.init_app(app)
    migrate.init_app(app, db)
    mail.init_app(app)


def register_blueprints(app):
    from src.api.users.blueprint import users_blueprint
    from src.api.collections.blueprint import collections_blueprint
    from src.api.quotes.blueprint import quotes_blueprint
    from src.api.reviews.blueprint import reviews_blueprint
    app.register_blueprint(users_blueprint, url_prefix='/')
    app.register_blueprint(quotes_blueprint, url_prefix='/')
    app.register_blueprint(collections_blueprint, url_prefix='/')
    app.register_blueprint(reviews_blueprint, url_prefix='/')


def register_err_handlers(app):
    from src.api import exceptions
    app.register_error_handler(Exception, src.api.exceptions.handle_general_exception)
    app.register_error_handler(exceptions.BadRequestException, src.api.exceptions.handle_exception)
    app.register_error_handler(exceptions.UnauthorizedException, src.api.exceptions.handle_exception)
    app.register_error_handler(exceptions.ForbiddenException, src.api.exceptions.handle_exception)
    app.register_error_handler(exceptions.NotFoundException, src.api.exceptions.handle_exception)
    app.register_error_handler(exceptions.ConflictException, src.api.exceptions.handle_exception)
    app.register_error_handler(exceptions.InternalServerErrorException, src.api.exceptions.handle_exception)
