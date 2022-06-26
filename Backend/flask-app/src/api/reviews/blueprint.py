from flask import request, Blueprint, current_app, url_for, Response

from src import User
from src.api.decorators import auth_token_required
from src.api.exceptions import BadRequestException, NotFoundException, ConflictException, ForbiddenException
from src.extensions import db
from src.models.quote import Quote
from src.models.review import Review
from src.utils.db_access import session_scope

reviews_blueprint = Blueprint('reviews', __name__)


@reviews_blueprint.route('/reviews', methods=['POST'])
@auth_token_required
def post_review(user_id):
    user = User.get(user_id)

    if not user.email_validation_date:
        raise ForbiddenException("Please validate your email address first.")

    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    quote_id = int(json_data.get('quote_id'))

    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException()

    text = json_data.get('text')
    rating = int(json_data.get('rating'))

    if db.session.query(Review).filter_by(quote_id=quote_id, user_id=user_id).first():
        raise ConflictException("Already reviewed.")

    review = Review(text, rating, quote_id, user_id)
    with session_scope():
        db.session.add(review)

    if review:
        return review.serialize, 201
    else:
        return {'message': "Already posted review."}, 400


@reviews_blueprint.route('/reviews', methods=['GET'])
@auth_token_required
def get_reviews(user_id):
    args = request.args
    page = args.get('page', 1, type=int)
    quote_id = args.get('quote_id', type=int)
    quote = Quote.get(quote_id)
    if not quote:
        raise NotFoundException()

    reviews = db.session.query(Review).order_by(Review.created_at.desc()).filter_by(quote_id=quote_id).paginate(page, int(current_app.config['REVIEWS_PER_PAGE']), False)

    user_review = db.session.query(Review).filter_by(quote_id=quote_id, user_id=user_id).first()

    next_url = url_for('reviews.get_reviews', page=reviews.next_num) if reviews.has_next else None
    prev_url = url_for('reviews.get_reviews', page=reviews.prev_num) if reviews.has_prev else None

    return {'pagination': {'page': reviews.page, 'per_page': reviews.per_page, 'total': reviews.total},
            'data': {'reviews': [r.serialize for r in reviews.items], 'reviewed': True if user_review else False},
            'links': {'next': next_url, 'prev': prev_url}
            }, 200


@reviews_blueprint.route('/reviews/<int:review_id>', methods=['PUT'])
@auth_token_required
def update_review(user_id, review_id):
    review = Review.get(review_id)
    if not review:
        raise NotFoundException()

    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    text = json_data.get('text')
    rating = int(json_data.get('rating'))

    with session_scope():
        review.text = text
        review.rating = rating

    return Response(status=200)


@reviews_blueprint.route('/reviews/<int:review_id>', methods=['DELETE'])
@auth_token_required
def delete_review(user_id, review_id):
    review = Review.get(review_id)
    if not review:
        raise NotFoundException()

    with session_scope():
        db.session.delete(review)

    return Response(status=200)
