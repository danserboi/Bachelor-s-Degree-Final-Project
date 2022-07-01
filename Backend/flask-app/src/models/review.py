from datetime import datetime

from sqlalchemy import Column, ForeignKey, Text, UniqueConstraint
from sqlalchemy import Integer, DateTime
from sqlalchemy.orm import relationship
from src.extensions import db
from src.utils.datetime_utils import utc_now


class Review(db.Model):
    id = Column(Integer, primary_key=True, autoincrement=True)

    created_at = Column(DateTime, default=utc_now())
    text = Column(Text, nullable=False)
    rating = Column(Integer, nullable=False)

    quote_id = Column(Integer, ForeignKey('quote.id'))
    quote = relationship('Quote', back_populates='reviews')

    user_id = Column(Integer, ForeignKey('user.id'))
    user = relationship('User', back_populates='reviews')

    __table_args__ = (UniqueConstraint('quote_id', 'user_id', name='_quote_id_user_id_review_uc'),)

    def __init__(self, text: str, rating: int, quote_id: int, user_id: int):
        self.text = text
        self.rating = rating
        self.quote_id = quote_id
        self.user_id = user_id

    def __repr__(self):
        return (
            f"Review: id={self.id}, rating={self.rating}, text={self.text}"
        )

    @property
    def serialize(self):
        info_dict = {
            'review_id': self.id,
            'text': self.text,
            'rating': self.rating,
            'quote_id': self.quote_id,
            'username': self.user.first_name + " " + self.user.last_name,
            'timestamp': int(dAatetime.timestamp(self.created_at))
        }

        return info_dict

    @staticmethod
    def get(review_id):
        return Review.query.get(review_id)

