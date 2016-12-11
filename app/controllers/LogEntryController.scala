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

class LogEntryController @Inject() (repo: LogEntryRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.logEntry_index(new MyDeadboltHandler, res))
    }
  }

  def getLogEntrysReport = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // to copy
  def show(id: Long) = Action.async { implicit request =>
    repo.getById(id).map { res =>
      Ok(views.html.logEntry_show(new MyDeadboltHandler, res(0)))
    }
  }

  def create(action: String, tableName1: String, userId: Long, userName: String, description: String) = {
    Await.result(repo.create(action, tableName1, userId, userName, description).map(res => print("DONE")), 3000.millis)
  }
}

case class CreateLogEntryForm(name: String, president: Long, description: String, companyId: Long)

// Update required
case class UpdateLogEntryForm(id: Long, name: String, president: Long, description: String, companyId: Long)
