package security

import be.objectify.deadbolt.scala.{ DynamicResourceHandler, DeadboltHandler }
import play.api.mvc.{ Request, Result, Results }
import be.objectify.deadbolt.core.models.Subject
import scala.concurrent.{ Future, Await }
import models.UserSecurity
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import dal._
import javax.inject._
import models._

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None, var rolesListParam: List[SecurityRole] = List()) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    Future(dynamicResourceHandler.orElse(Some(new MyDynamicResourceHandler())))
  }

  override def getSubject[A](request: Request[A]): Future[Option[Subject]] = {
    var rol: String = request.session.get("role").get
    var user1 = new UserSecurity("steve", rol, rolesListParam)

    Future(Some(user1))
  }

  def onAuthFailure[A](request: Request[A]): Future[Result] = {
    Future { Results.Forbidden(views.html.accessFailed()) }
  }
}