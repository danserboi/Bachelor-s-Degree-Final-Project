from flask_mail import Message
from run import celery
from src.extensions import mail
from src.utils.db_access import session_scope
from src.models.token_blacklist import BlacklistedToken


@celery.task
def send_email(recipient, subject, html_body):
    msg = Message(recipients=[recipient], subject=subject)
    msg.html = html_body
    mail.send(msg)


@celery.task
def delete_expired_blacklisted_jwt_tokens():
    with session_scope():
        BlacklistedToken.delete_all_by_expiration_date()
