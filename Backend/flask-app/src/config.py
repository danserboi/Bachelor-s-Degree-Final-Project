import os


class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY')
    SQLALCHEMY_DATABASE_URI = os.environ.get('DB_URL')
    RABBITMQ_URL = os.environ.get('RABBITMQ_URL')
    REDIS_URL = os.environ.get('REDIS_URL')
    MAIL_SERVER = os.environ.get('MAIL_SERVER')
    MAIL_PORT = os.environ.get('MAIL_PORT')
    MAIL_USE_TLS = os.environ.get('MAIL_USE_TLS').lower() == 'true'
    MAIL_USE_SSL = os.environ.get('MAIL_USE_SSL').lower() == 'true'
    MAIL_USERNAME = os.environ.get('MAIL_USERNAME')
    MAIL_PASSWORD = os.environ.get('MAIL_PASSWORD')
    MAIL_DEFAULT_SENDER = os.environ.get('MAIL_DEFAULT_SENDER')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    DEBUG = False
    TESTING = False
    TOKEN_EXPIRE_HOURS = 0
    TOKEN_EXPIRE_MINUTES = 0
    EMAIL_TOKEN_EXPIRE_HOURS = 0
    EMAIL_TOKEN_EXPIRE_MINUTES = 0
    RESET_PIN_EXPIRE_MINUTES = 5
    CELERY_BEAT_PERIOD_SECONDS = 3600.0
    CELERY_BEAT_EXPIRES_SECONDS = 60.0
    RESET_PIN_LENGTH = 8
    QUOTES_PER_PAGE = 50
    REVIEWS_PER_PAGE = 50
    HOST = "192.168.0.101"


class TestingConfig(Config):
    DEBUG = True
    TESTING = True
    BCRYPT_LOG_ROUNDS = 6
    TOKEN_EXPIRE_SECONDS = 1
    EMAIL_TOKEN_EXPIRE_HOURS = 24
    EMAIL_TOKEN_EXPIRE_MINUTES = 0


class DevelopmentConfig(Config):
    DEBUG = True
    BCRYPT_LOG_ROUNDS = 6
    TOKEN_EXPIRE_MINUTES = 60
    EMAIL_TOKEN_EXPIRE_MINUTES = 60


class ProductionConfig(Config):
    BCRYPT_LOG_ROUNDS = 12
    TOKEN_EXPIRE_HOURS=168
