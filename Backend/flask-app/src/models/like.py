from sqlalchemy import Column, ForeignKey, UniqueConstraint
from sqlalchemy import Integer, DateTime
from sqlalchemy.orm import relationship
from src.extensions import db
from src.utils.datetime_utils import utc_now


class Like(db.Model):
    id = Column(Integer, primary_key=True, autoincrement=True)

    created_at = Column(DateTime, default=utc_now())

    quote_id = Column(Integer, ForeignKey('quote.id'))
    quote = relationship('Quote', back_populates='likes')

    user_id = Column(Integer, ForeignKey('user.id'))
    user = relationship('User', back_populates='likes')

    __table_args__ = (UniqueConstraint('quote_id', 'user_id', name='_quote_id_user_id_like_uc'),)

    def __init__(self, quote_id: int, user_id: int):
        self.quote_id = quote_id
        self.user_id = user_id

    def __repr__(self):
        return (
            f"Like: id={self.id}, created_at={self.created_at}, user_id={self.user_id}"
        )

    @property
    def serialize(self):
        info_dict = {
            'id': self.id,
            'quote': self.quote.serialize,
            'user': self.user,
        }

        return info_dict

    @staticmethod
    def get(quote_id, user_id):
        return db.session.query(Like).filter_by(quote_id=quote_id, user_id=user_id).first()
