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
import security.MyDeadboltHandler

class LoginController @Inject() (repo: UserRepository, repoSettings: SettingRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[LoginForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "password" -> nonEmptyText)(LoginForm.apply)(LoginForm.unapply)
  }

  def index = LanguageAction {
    Ok(views.html.login(newForm))
  }

  def logout = LanguageAction {
    Redirect("/login").withNewSession
  }

  def getCompanyInfo(): Setting = {
    Await.result(repoSettings.getFirst().map {
      case (res1) =>
        res1(0)
    }, 3000.millis)
  }

  def login = LanguageAction { implicit request =>

    newForm.bindFromRequest.fold(
      errorForm => {
        Ok(views.html.login(errorForm))
      },
      res => {
        val company = getCompanyInfo()
        println(company)
        Await.result(repo.getByLogin(res.user, res.password).map { res2 =>
          if (res2.length > 0) {
            Ok("Welcome!").withSession(
              "userSecurity" -> res2(0).login,
              "role" -> res2(0).type_1.toLowerCase,
              "userId" -> res2(0).id.toString(),
              "userName" -> res2(0).name.toString(),
              "lang" -> company.language)
            if (res2(0).type_1.toLowerCase == "admin") {
              Redirect("/").withSession("userSecurity" -> res2(0).login,
                "role" -> res2(0).type_1.toLowerCase,
                "userId" -> res2(0).id.toString(),
                "userName" -> res2(0).name.toString(),
                "lang" -> company.language)
            } else if (res2(0).type_1.toLowerCase == "employee") {
              Redirect("/").withSession("userSecurity" -> res2(0).login,
                "role" -> res2(0).type_1.toLowerCase,
                "userId" -> res2(0).id.toString(),
                "userName" -> res2(0).name.toString(),
                "lang" -> company.language)
            } else if (res2(0).type_1.toLowerCase == "insumo") {
              Redirect("/").withSession("userSecurity" -> res2(0).login,
                "role" -> res2(0).type_1.toLowerCase,
                "userId" -> res2(0).id.toString(),
                "userName" -> res2(0).name.toString(),
                "lang" -> company.language)
            } else if (res2(0).type_1.toLowerCase == "store") {
              Redirect("/").withSession("userSecurity" -> res2(0).login,
                "role" -> res2(0).type_1.toLowerCase,
                "userId" -> res2(0).id.toString(),
                "userName" -> res2(0).name.toString(),
                "lang" -> company.language)
            } else if (res2(0).type_1.toLowerCase == "contabilidad") {
              Redirect("/").withSession("userSecurity" -> res2(0).login,
                "role" -> res2(0).type_1.toLowerCase,
                "userId" -> res2(0).id.toString(),
                "userName" -> res2(0).name.toString(),
                "lang" -> company.language)
            } else {
              Redirect("/error")
            }
          } else {
            Ok(views.html.login(newForm))
          }
        }, 3000.millis)
      })
  }

}

case class LoginForm(user: String, password: String)
