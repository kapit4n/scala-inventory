package controllers

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._
import it.innove.play.pdf.PdfGenerator
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class SettingController @Inject() (repo: SettingRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val companyes = scala.collection.immutable.Map[String, String]("0" -> "Ninguno", "1" -> "Company 1", "2" -> "Company 2")

  // update required
  val updateForm: Form[UpdateSettingForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> text,
      "president" -> text,
      "description" -> text)(UpdateSettingForm.apply)(UpdateSettingForm.unapply)
  }

  var updatedRow: Setting = _
  // to copy
  def show() = LanguageAction.async { implicit request =>
    repo.getFirst().map { res =>
      if (res.size > 0) {
        updatedRow = res(0)
      } else {
        updatedRow = Setting(0, "Name of Company", "Name CEO", "Description of the Company")
      }
      Ok(views.html.setting_show(new MyDeadboltHandler, updatedRow))
    }
  }

  // update required
  def getUpdate = LanguageAction.async { implicit request =>
    repo.getFirst().map { res =>
      var anyData: Map[String, String] = Map[String, String]()
      if (res.size == 0) {
        anyData = Map(
          "id" -> "0",
          "name" -> "Name Company",
          "president" -> "Name CEO",
          "description" -> "Description of the empresa")
      } else {
        updatedRow = res(0)
        anyData = Map(
          "id" -> updatedRow.id.toString,
          "name" -> updatedRow.name.toString,
          "president" -> updatedRow.president.toString,
          "description" -> updatedRow.description.toString)
      }
      Ok(views.html.setting_update(new MyDeadboltHandler, updatedRow, updateForm.bind(anyData)))
    }
  }

  // update required
  def updatePost = LanguageAction.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.setting_update(new MyDeadboltHandler, updatedRow, errorForm)))
      },
      res => {
        if (res.id > 0) {
          repo.update(res.id, res.name, res.president, res.description).map { _ =>
            Redirect(routes.SettingController.show())
          }
        } else {
          repo.create(res.name, res.president, res.description).map { _ =>
            Redirect(routes.SettingController.show())
          }
        }
      })
  }
}

// Update required
case class UpdateSettingForm(id: Long, name: String, president: String, description: String)
