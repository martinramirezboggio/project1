package repositories

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.modules.reactivemongo._
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import models.Post
import reactivemongo.api.commands.{MultiBulkWriteResult, WriteResult}


class PostRepository @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi){

  private def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("posts"))

  def list(limit: Int = 100): Future[Seq[Post]] = {
    collection.flatMap( _
      .find(BSONDocument())
      .cursor[Post](ReadPreference.primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[Post]]())
    )
  }

  def create(post: List[Post]): Future[MultiBulkWriteResult] =
    collection.flatMap(_.insert.many(post))

  def read(id: BSONObjectID): Future[Option[Post]] =
    collection.flatMap( _.find(BSONDocument("_id" -> id)).one[Post])

  def update(id: BSONObjectID, post: Post): Future[Option[Post]] =
    collection.flatMap( _
      .findAndUpdate(
        BSONDocument("_id" -> id),
        BSONDocument(
          f"$$set" -> BSONDocument(
            "title" -> post.title,
            "description" -> post.description
          )
        ),true
      ).map(_.result[Post])
    )

  def destroy(id: BSONObjectID): Future[Option[Post]] =
    collection.flatMap(_.findAndRemove(BSONDocument("_id" -> id)).map( _.result[Post]))

}
