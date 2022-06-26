from src.extensions import db
from src.models.like import Like
from src.models.quote import Quote
from src.utils.db_access import session_scope
from sqlalchemy.sql import func
from sqlalchemy.orm import load_only


def random_quote():
    return Quote.query.options(load_only('id')).offset(
            func.floor(
                func.random() *
                db.session.query(func.count(Quote.id))
            )
        ).limit(1).all()


def user_gives_like(user_id, quote_id):
    if Like.get(quote_id, user_id):
        return None
    like = Like(quote_id=quote_id, user_id=user_id)
    with session_scope():
        db.session.add(like)
    return like


def user_dislikes_quote(user_id, quote_id):
    like = Like.get(quote_id, user_id)
    if not like:
        return None
    with session_scope():
        db.session.delete(like)
    return True

