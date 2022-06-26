from datetime import datetime

from sqlalchemy import Table, Column, ForeignKey, UniqueConstraint
from sqlalchemy import Integer, String, DateTime
from sqlalchemy.orm import relationship
from src.extensions import db
from src.utils.datetime_utils import utc_now

collection_quote_association_table = Table('collection_quote_association', db.metadata,
                                           Column('quote_id', Integer, ForeignKey('quote.id')),
                                           Column('collection_id', Integer, ForeignKey('collection.id'))
                                           )


class Collection(db.Model):
    id = Column(Integer, primary_key=True, autoincrement=True)

    name = Column(String(255), nullable=False, unique=True)
    created_at = Column(DateTime, default=utc_now())

    owner_id = Column(Integer, ForeignKey('user.id'))
    owner = relationship('User', back_populates='collections')

    quotes = relationship('Quote',
                          secondary=collection_quote_association_table,
                          back_populates='collections')

    __table_args__ = (UniqueConstraint('owner_id', 'name', name='_owner_id_name_uc'),)

    def __init__(self, name: str, owner_id: int):
        self.name = name
        self.owner_id = owner_id

    def __repr__(self):
        return (
            f"Collection: id={self.id}, created_at={self.created_at}, name={self.name}"
        )

    @property
    def serialize(self):
        info_dict = {
            'id': self.id,
            'name': self.name,
            'created_at': int(datetime.timestamp(self.created_at)),
            'quotes': [q.bulk_data(self.owner_id) for q in self.quotes]
        }
        return info_dict

    @staticmethod
    def get(collection_id):
        return Collection.query.get(collection_id)
