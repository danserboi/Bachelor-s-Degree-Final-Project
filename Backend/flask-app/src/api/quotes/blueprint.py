from flask import request, jsonify, Blueprint, current_app, url_for, Response

from src import User
from src.api.decorators import auth_token_required
from src.api.exceptions import BadRequestException, UnauthorizedException, NotFoundException, ForbiddenException
from src.api.quotes.business import user_gives_like, user_dislikes_quote, random_quote
from src.extensions import db
from src.models.collection import Collection
from src.models.quote import Quote
from src.utils.db_access import session_scope

quotes_blueprint = Blueprint('quotes', __name__)


@quotes_blueprint.route('/quotes', methods=['POST'])
@auth_token_required
def add_quote(user_id):
    user = User.get(user_id)

    if not user.email_validation_date:
        raise ForbiddenException("Please validate your email address first.")

    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    author = json_data.get('author')
    text = json_data.get('text')
    genre = json_data.get('genre')
    is_public = bool(json_data.get('is_public'))
    collection_id = json_data.get('collection_id')
    if not author or not text or not genre:
        raise BadRequestException()

    quote = Quote(author=author, text=text, genre=genre, is_public=is_public, owner_id=user_id,
                  collection_id=collection_id)
    with session_scope():
        db.session.add(quote)

    return Response(status=201)


@quotes_blueprint.route('/quotes', methods=['GET'])
@auth_token_required
def get_quotes(user_id):
    args = request.args
    author = args.get('author')
    genre = args.get('genre')
    kwargs = {'is_public': True}

    quotes = db.session.query(Quote).order_by(Quote.created_at.desc()).filter_by(**kwargs)
    if author:
        quotes = quotes.filter(Quote.author.contains(author))
    if genre:
        quotes = quotes.filter(Quote.genre.contains(genre))
    page = args.get('page', 1, type=int)
    quotes = quotes.paginate(page, int(current_app.config['QUOTES_PER_PAGE']), False)

    next_url = url_for('quotes.get_quotes', page=quotes.next_num) if quotes.has_next else None
    prev_url = url_for('quotes.get_quotes', page=quotes.prev_num) if quotes.has_prev else None

    return {'pagination': {'page': quotes.page, 'per_page': quotes.per_page, 'total': quotes.total},
            'data': {'quotes': [q.bulk_data(user_id) for q in quotes.items]},
            'links': {'next': next_url, 'prev': prev_url}
            }, 200


@quotes_blueprint.route('/quotes/liked', methods=['GET'])
@auth_token_required
def get_liked_quotes(user_id):
    return jsonify({'quotes': [like.quote.bulk_data(user_id) for like in User.get(user_id).likes]}), 200


@quotes_blueprint.route('/quotes/owned', methods=['GET'])
@auth_token_required
def get_my_quotes(user_id):
    return jsonify({'quotes': [quote.bulk_data(user_id) for quote in User.get(user_id).quotes]}), 200


@quotes_blueprint.route('/quotes/<int:quote_id>', methods=['GET'])
@auth_token_required
def get_quote(user_id, quote_id):
    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException(message="Quote doesn't exist.")
    return quote.bulk_data(user_id), 200


@quotes_blueprint.route('/quotes/random', methods=['GET'])
@auth_token_required
def get_random_quote(user_id):
    quote = random_quote()[0]
    if not quote:
        raise NotFoundException(message="No quote in database.")
    return jsonify(quote.bulk_data(user_id)), 200


@quotes_blueprint.route('/quotes/daily', methods=['GET'])
def get_daily_quote():
    quote = random_quote()[0]
    if not quote:
        raise NotFoundException(message="No quote in database.")
    return jsonify({
            'id': int(quote.id),
            'author': quote.author,
            'text': quote.text}), 200


@quotes_blueprint.route('/quotes/<int:quote_id>', methods=['PUT'])
@auth_token_required
def update_quote(user_id, quote_id):
    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException()
    if quote.owner_id != user_id:
        raise ForbiddenException()
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    collection_id = json_data.get('collection_id')
    if not collection_id:
        raise BadRequestException()

    with session_scope():
        if collection_id:
            quote.collections.append(Collection.get(collection_id))

    if quote:
        return Response(status=200)


@quotes_blueprint.route('/quotes/<int:quote_id>', methods=['DELETE'])
@auth_token_required
def delete_quote(user_id, quote_id):
    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException()
    if quote.owner_id != user_id:
        raise UnauthorizedException()
    with session_scope():
        db.session.delete(quote)
    return Response(status=200)


@quotes_blueprint.route('/quotes/<int:quote_id>/like', methods=['POST'])
@auth_token_required
def like_quote(user_id, quote_id):
    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException()
    like = user_gives_like(user_id, quote_id)
    if like:
        return Response(status=200)
    else:
        return {'message': "Already liked this quote."}, 400


@quotes_blueprint.route('/quotes/<int:quote_id>/like', methods=['DELETE'])
@auth_token_required
def dislike_quote(user_id, quote_id):
    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException()
    like = user_dislikes_quote(user_id, quote_id)
    if like:
        return Response(status=200)
    else:
        return {'message': "This quote didn't have any like from you."}, 400



