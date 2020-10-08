package controllers

import javax.inject.{Inject, Singleton}
import models.Post
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import reactivemongo.bson.BSONObjectID
import repositories.PostRepository

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by martinramirezboggio on 08/10/2020
 */

@Singleton
class PostController @Inject()(
                              implicit val ec: ExecutionContext,
                              val controllerComponents: ControllerComponents,
                              postRepository: PostRepository
                              ) extends BaseController {

  def posts: Action[AnyContent] = Action.async {
    postRepository.list().map{ posts =>
      Ok(Json.toJson(posts))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) {
    _.body
      .validate[List[Post]]
      .map { post =>
        postRepository.create(post).map{ _ =>
          Ok(Json.toJson(post))
        }
      }
      .getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  def read(id: BSONObjectID): Action[AnyContent] = Action.async {
    postRepository.read(id).map { maybePost =>
      maybePost.map { post =>
        Ok(Json.toJson(post))
      }.getOrElse(NotFound)
    }
  }

  def update(id: BSONObjectID): Action[JsValue] = Action.async(parse.json) {
    _.body
      .validate[Post]
      .map { post =>
        postRepository.update(id, post).map {
          case Some(post) => Ok(Json.toJson(post))
          case _ => NotFound
        }
      }
      .getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  def destroy(id: BSONObjectID): Action[AnyContent] = Action.async {
    postRepository.destroy(id).map {
      case Some(post) => Ok(Json.toJson(post))
      case _ => NotFound
    }
  }

}
