from src.extensions import db
from src.utils.datetime_utils import utc_now, datetime_aware_from_timestamp


class BlacklistedToken(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    token = db.Column(db.String(255), unique=True, nullable=False)
    blacklist_time = db.Column(db.DateTime, default=utc_now)
    expires_at = db.Column(db.DateTime, nullable=False)

    def __init__(self, token, expires_at):
        self.token = token
        self.expires_at = datetime_aware_from_timestamp(expires_at)

    def __repr__(self):
        return (
            f"BlacklistedToken: token={self.token}, blacklist_time={self.blacklist_time}, expires_at={self.expires_at}"
        )

    @classmethod
    def is_blacklisted(cls, token):
        return True if cls.query.filter_by(token=token).first() else False

    @classmethod
    def delete_all_by_expiration_date(cls):
        now = utc_now()
        cls.query.filter(cls.expires_at <= now).delete()
