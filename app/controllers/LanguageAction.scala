package controllers
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ ExecutionContext, Future, Await }

import javax.inject._
import it.innove.play.pdf.PdfGenerator
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

object LanguageAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {

  val newRequest = new WrappedRequest[A](request) {
      //calculate from request url
      val lang = Lang("es")
      override lazy val acceptLanguages = Seq(lang)
    }
    block(newRequest)
  }
}