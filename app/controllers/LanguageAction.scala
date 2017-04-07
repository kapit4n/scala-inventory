package controllers
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import scala.concurrent.{ ExecutionContext, Future, Await }

import it.innove.play.pdf.PdfGenerator
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

object LanguageAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {

  val newRequest = new WrappedRequest[A](request) {
      //calculate from request url
      val lang = Lang(request.session.get("lang").getOrElse("us").toString)
      override lazy val acceptLanguages = Seq(lang)
    }
    block(newRequest)
  }
}
