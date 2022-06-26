import unittest
from flask.cli import FlaskGroup
from run import app
import coverage
from src.extensions import db
from src.models.quote import Quote
from src.models.user import User
from src.utils.db_access import session_scope
import json

cli = FlaskGroup(app)


@cli.command()
def test():
    """Runs unit tests."""
    tests = unittest.TestLoader().discover('tests', pattern='test_*.py')
    unittest.TextTestRunner(verbosity=2).run(tests)


@cli.command('test_coverage')
def test_coverage():
    """Runs unit tests and show code coverage."""
    code_coverage = coverage.coverage(
        include='src/*',
        branch=True,
    )
    code_coverage.start()
    tests = unittest.TestLoader().discover('tests', pattern='test_*.py')
    result = unittest.TextTestRunner(verbosity=2).run(tests)
    if result.wasSuccessful():
        code_coverage.stop()
        code_coverage.save()
        code_coverage.report()
        code_coverage.html_report()
        code_coverage.erase()


@cli.command('db_creation')
def db_creation():
    """Creates all tables."""
    db.create_all()


@cli.command('db_recreation')
def db_recreation():
    """Drops and recreates all tables."""
    db.drop_all()
    db.create_all()


@cli.command('db_drop')
def db_drop():
    """Drops all tables."""
    db.drop_all()


@cli.command('db_populate')
def db_populate():
    """Populates the database with a small initial set of data."""
    user1 = User(email="ibrahim.trello@gmail.com", password="Qwer1234", first_name="Ibrahim", last_name="Trello")
    user2 = User(email="anonim@anonim.com", password="ewqr5421REA", first_name="Anonim", last_name="Anonimus")
    with session_scope():
        db.session.add(user1)
        db.session.add(user2)


@cli.command('db_add_quotes')
def db_add_quotes():
    """ Populates the database with all the Quotes"""
    user = User(email="ibrahim.trello@gmail.com", password="Qwer1234", first_name="Ibrahim", last_name="Trello", admin=True)
    try:
        with session_scope():
            db.session.add(user)
    except:
        pass
    f = open('all_quotes.json')
    data = json.load(f)
    for x in data:
        text = x['quoteText']
        author = x['quoteAuthor']
        genre = x['quoteGenre']
        quote = Quote(author=author, text=text, genre=genre, owner_id=user.id, is_public=True, collection_id=None)
        try:
            with session_scope():
                db.session.add(quote)
        except:
            pass
    f.close()


if __name__ == '__main__':
    cli()
