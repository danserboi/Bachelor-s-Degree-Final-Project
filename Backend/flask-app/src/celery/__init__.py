from celery import Celery


def make_celery(app):
    celery = Celery(app.import_name, include=['src.celery.tasks'], broker=app.config['RABBITMQ_URL'],
                    backend=app.config['REDIS_URL'])
    celery.conf.update(app.config)
    celery.conf.beat_schedule = {
        "delete-expired-blacklisted-jwt-tokens": {
            "task": "src.celery.tasks.delete_expired_blacklisted_jwt_tokens",
            "schedule": app.config['CELERY_BEAT_PERIOD_SECONDS'],
            'options': {
                'expires': app.config['CELERY_BEAT_EXPIRES_SECONDS'],
            },
        }
    }

    class ContextTask(celery.Task):
        def __call__(self, *args, **kwargs):
            with app.app_context():
                return self.run(*args, **kwargs)

    celery.Task = ContextTask
    return celery
