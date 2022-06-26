from contextlib import contextmanager
from src.api.exceptions import InternalServerErrorException
from src.extensions import db


@contextmanager
def session_scope():
    """Provide a transactional scope around a series of operations.
    https://docs.sqlalchemy.org/en/13/orm/session_basics.html"""
    try:
        yield db.session
        db.session.commit()
    except:
        db.session.rollback()
        raise InternalServerErrorException()
