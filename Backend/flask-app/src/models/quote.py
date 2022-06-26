from sqlalchemy import Column, ForeignKey, Boolean
from sqlalchemy import Integer, String, Text, DateTime
from sqlalchemy.orm import relationship, validates

from src import User
from src.extensions import db
from src.models.collection import collection_quote_association_table, Collection
from src.models.like import Like
from src.utils.datetime_utils import utc_now


class Quote(db.Model):
    id = Column(Integer, primary_key=True, autoincrement=True)

    author = Column(String(255), nullable=False)
    text = Column(Text, nullable=False, unique=True)
    genre = Column(String(255), nullable=True)
    is_public = Column(Boolean, default=False)
    created_at = Column(DateTime, default=utc_now())

    owner_id = Column(Integer, ForeignKey('user.id'))
    owner = relationship('User', back_populates='quotes')

    likes = relationship('Like', back_populates='quote', cascade="all, delete")
    reviews = relationship('Review', back_populates='quote', cascade="all, delete")
    collections = relationship('Collection',
                               secondary=collection_quote_association_table,
                               back_populates='quotes')

    @validates('author', 'text', 'genre')
    def _write_once(self, key, value):
        existing = getattr(self, key)
        if existing is not None:
            raise ValueError("Field '%s' is write-once." % key)
        return value

    def __init__(self, author: str, text: str, genre: str, is_public: bool, owner_id: int, collection_id: int):
        self.author = author
        self.text = text
        self.genre = genre.lower()
        self.is_public = is_public
        self.owner_id = owner_id
        if is_public:
            self.is_public = is_public
        if collection_id:
            self.collections.append(Collection.get(collection_id))

    def __repr__(self):
        return (
            f"Quote: id={self.id}, author={self.author}, text={self.text}"
        )

    @property
    def serialize(self):
        info_dict = {
            'id': self.id,
            'author': self.author,
            'text': self.text,
            'likes_count': len(self.likes),
            'owner_id': self.owner_id,
            'genre': self.genre,
            'collections': [g.id for g in self.collections]
        }

        return info_dict

    def bulk_data(self, user_id):
        reviews_avg = 0
        if len(self.reviews) > 0:
            reviews_avg = sum([review.rating for review in self.reviews]) / len(self.reviews)
        info_dict = {
            'id': self.id,
            'author': self.author,
            'text': self.text,
            'liked': True if Like.get(self.id, user_id) else False,
            'likes_count': len(self.likes),
            'owner_name': User.get(self.owner_id).first_name + " " + User.get(self.owner_id).last_name,
            'genre': self.genre,
            'reviews_no': len(self.reviews),
            'reviews_avg': reviews_avg
        }

        return info_dict

    @staticmethod
    def get(quote_id):
        return Quote.query.get(quote_id)
