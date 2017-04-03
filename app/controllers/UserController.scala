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
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class UserController @Inject() (
  repo: UserRepository,
  repoRoles: UserRoleRepository,
  val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "carnet" -> number,
      "phone" -> number,
      "address" -> text,
      "Salary" -> number,
      "type_1" -> text,
      "login" -> text,
      "password" -> text)(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  var updateRow: User = _

  val loginForm: Form[LoginForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "password" -> nonEmptyText)(LoginForm.apply)(LoginForm.unapply)
  }

  val types = scala.collection.immutable.Map[String, String]("Employee" -> "Employee", "Insumo" -> "Insumo", "Admin" -> "Admin", "Store" -> "Store", "Contabilidad" -> "Contabilidad")

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.user_index(new MyDeadboltHandler, res))
    }
  }

  def addGet = Action { implicit request =>
    Ok(views.html.user_add(new MyDeadboltHandler, newForm, types))
  }

  def profile() = Action { implicit request =>
    Await.result(repo.getById(request.session.get("userId").getOrElse("0").toLong).map { res2 =>
      if (res2.length > 0) {
        Redirect("/")
      } else {
        Redirect("/login")
      }
    }, 2000.millis)
  }

  def profileById(userId: Long) = Action { implicit request =>
    Await.result(repo.getById(userId).map { res2 =>
      Redirect("/")
    }, 2000.millis)
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.user_add(new MyDeadboltHandler, errorForm, types)))
      },
      res => {
        repo.create(
          res.name, res.carnet, res.phone, res.address,
          res.Salary, res.type_1, res.login, res.password,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { _ =>
            Redirect(routes.UserController.index)
          }
      })
  }

  def getUsers = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // Update required
  val updateForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "phone" -> number.verifying(min(0), max(9999999)),
      "address" -> nonEmptyText,
      "Salary" -> number,
      "type_1" -> text,
      "login" -> text,
      "password" -> text)(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def getRoles(): Seq[Roles] = {
    Await.result(repoRoles.listRoles().map(res => res), 3000.millis)
  }

  def getAssignedRoles(id: Long): Seq[UserRole] = {
    Await.result(repoRoles.listUserRoles(id).map(res => res), 3000.millis)
  }

  var userId: Long = _
  // to copy
  def show(id: Long) = Action.async { implicit request =>
    userId = id
    var assignedRoles = getAssignedRoles(id)
    var roles = getRoles()
    repo.getById(id).map { res =>
      Ok(views.html.user_show(new MyDeadboltHandler, res(0), roles, assignedRoles))
    }
  }

  def getRoleByCode(roleCode: String): Roles = {
    Await.result(repoRoles.getRoleByCode(roleCode).map(res => res(0)), 3000.millis)
  }

  // to copy
  def assignRole(userId: Long, roleCode: String) = Action.async { implicit request =>
    var rol = getRoleByCode(roleCode)
    repoRoles.createUserRole(userId, rol.roleName, roleCode).map(res =>
      Redirect(routes.UserController.show(res.userId)))
  }

  // to copy
  def removeRole(id: Long) = Action.async { implicit request =>
    repoRoles.deleteUserRole(id).map(res =>
      Redirect(routes.UserController.show(userId)))
  }

  // update required
  def getUpdate(id: Long) = Action.async { implicit request =>
    repo.getById(id).map { res =>
      updateRow = res(0)
      val anyData = Map(
        "id" -> id.toString().toString(), "name" -> updateRow.name,
        "carnet" -> updateRow.carnet.toString(), "phone" -> updateRow.phone.toString(),
        "address" -> updateRow.address, "Salary" -> updateRow.Salary.toString(),
        "type_1" -> updateRow.type_1.toString(), "login" -> updateRow.login.toString(),
        "password" -> updateRow.password.toString())
      Ok(views.html.user_update(new MyDeadboltHandler, updateRow, updateForm.bind(anyData), types))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Redirect(routes.UserController.index)
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  def updatePost = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.user_update(new MyDeadboltHandler, updateRow, errorForm, types)))
      },
      res => {
        repo.update(
          res.id, res.name, res.carnet, res.phone,
          res.address, res.Salary, res.type_1,
          res.login, res.password,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { _ =>
            Redirect(routes.UserController.show(res.id))
          }
      })
  }
}

case class CreateUserForm(name: String, carnet: Int, phone: Int, address: String, Salary: Int, type_1: String, login: String, password: String)

case class UpdateUserForm(id: Long, name: String, carnet: Int, phone: Int, address: String, Salary: Int, type_1: String, login: String, password: String)