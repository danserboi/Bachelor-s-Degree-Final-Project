from flask import request, Blueprint, Response

from flask_accept import accept

from src import User
from src.api.decorators import auth_token_required
from src.api.exceptions import BadRequestException, NotFoundException, ConflictException, ForbiddenException
from src.extensions import db
from src.models.collection import Collection
from src.utils.db_access import session_scope

collections_blueprint = Blueprint('collections', __name__)


@collections_blueprint.route('/collections', methods=['POST'])
@accept('application/json')
@auth_token_required
def create_collection(user_id):
    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    name = json_data.get('name')
    if not name:
        raise BadRequestException()

    if db.session.query(Collection).filter_by(name=name, owner_id=user_id).first():
        return ConflictException("Collection already exist, try another name!")

    collection = Collection(name=name, owner_id=user_id)
    with session_scope():
        db.session.add(collection)

    return collection.serialize, 201


@collections_blueprint.route('/collections', methods=['GET'])
@auth_token_required
def collections(user_id):
    user = User.get(user_id)
    if user:
        return {'collections': [coll.serialize for coll in user.collections]}, 200
    else:
        return NotFoundException("User not found.")


@collections_blueprint.route('/collections/<int:collection_id>', methods=['GET'])
@auth_token_required
def get_collection_quotes(user_id, collection_id):
    collection = db.session.query(Collection).filter_by(id=collection_id, owner_id=user_id).first()
    if not collection:
        return NotFoundException()
    if collection.owner_id != user_id:
        return ForbiddenException()

    return {'quotes': [quote.bulk_data(user_id) for quote in collection.quotes]}, 200


@collections_blueprint.route('/collections/<int:collection_id>', methods=['PUT'])
@auth_token_required
def update_collection(user_id, collection_id):
    collection = db.session.query(Collection).filter_by(id=collection_id, owner_id=user_id).first()
    if not collection:
        return NotFoundException()
    if collection.owner_id != user_id:
        return ForbiddenException()

    json_data = request.get_json()
    if not json_data:
        raise BadRequestException()
    name = json_data.get('name')
    if not name:
        raise BadRequestException()

    if db.session.query(Collection).filter_by(name=name, owner_id=user_id).first():
        return ConflictException("Collection already exist, try another name!")
    else:
        with session_scope():
            collection.name = name

    return Response(status=200)


@collections_blueprint.route('/collections/<int:collection_id>', methods=['DELETE'])
@auth_token_required
def delete_collection(user_id, collection_id):
    collection = db.session.query(Collection).filter_by(id=collection_id, owner_id=user_id).first()
    if not collection:
        return NotFoundException()
    if collection.owner_id != user_id:
        return ForbiddenException()

    with session_scope():
        db.session.delete(collection)

    return Response(status=200)
