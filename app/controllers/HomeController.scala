package controllers

import javax.inject._
import play.api._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.PostRepository

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
                              implicit val ec: ExecutionContext,
                              val controllerComponents: ControllerComponents,
                              postRepository: PostRepository
                              ) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok("App works")
  }

  // Testing functions
  def ping: Action[AnyContent] = Action { implicit request =>
    Ok("String works")
  }

  def json: Action[AnyContent] = Action { _ =>
    Ok(Json.obj("yes" -> true))
  }

  def posted: Action[JsValue] = Action(parse.json) { implicit request =>
    Ok(Json.toJson(request.body))
  }
}
