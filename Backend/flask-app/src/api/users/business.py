from flask import render_template, url_for, current_app
from src.celery.tasks import send_email


def send_welcome_email(user, token):
    href = current_app.config.get("HOST") + '/' + url_for('users.confirm_email', token='')[1:] + token
    send_email.delay(
        recipient=user.email,
        subject=f"Welcome to Quotes for Mind and Soul, {user.first_name}",
        html_body=render_template("users/welcome.html", user=user, href=href))


def send_email_validation_email(user, token):
    href = current_app.config.get("HOST") + '/' + token
    send_email.delay(
        recipient=user.email,
        subject="Quotes for Mind and Soul Email Validation",
        html_body=render_template("users/email_validation.html", user=user, href=href))


def send_password_recovery_email(user, reset_pin):
    send_email.delay(
        recipient=user.email,
        subject=f"Password Recovery for Quotes for Mind and Soul",
        html_body=render_template("users/password_recovery.html", user=user, reset_pin=reset_pin))
