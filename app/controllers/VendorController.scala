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
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class VendorController @Inject() (repo: VendorRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateVendorForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "telefono" -> number,
      "direccion" -> nonEmptyText,
      "contacto" -> nonEmptyText,
      "account" -> longNumber)(CreateVendorForm.apply)(CreateVendorForm.unapply)
  }

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.vendor_index(new MyDeadboltHandler, res))
    }
  }

  def addGet = Action { implicit request =>
    Ok(views.html.vendor_add(new MyDeadboltHandler, newForm))
  }

  def addVendor = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.vendor_add(new MyDeadboltHandler, errorForm)))
      },
      vendor => {
        repo.create(
          vendor.name, vendor.telefono, vendor.direccion,
          vendor.contacto, vendor.account,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.VendorController.show(resNew.id))
          }
      })
  }

  def getVendores = Action.async {
    repo.list().map { vendores =>
      Ok(Json.toJson(vendores))
    }
  }

  // update required
  val updateForm: Form[UpdateVendorForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "telefono" -> number.verifying(min(0), max(9999999)),
      "direccion" -> nonEmptyText,
      "contacto" -> text,
      "account" -> longNumber)(UpdateVendorForm.apply)(UpdateVendorForm.unapply)
  }

  // to copy
  def show(id: Long) = Action.async { implicit request =>
    repo.getById(id).map { res =>
      Ok(views.html.vendor_show(new MyDeadboltHandler, res(0)))
    }
  }

  // to copy
  def profile(id: Long) = Action {
    Redirect(routes.VendorController.show(id))
  }

  var updatedRow: Vendor = _

  // update required
  def getUpdate(id: Long) = Action.async { implicit request =>
    repo.getById(id).map { res =>
      updatedRow = res(0)
      val anyData = Map("id" -> id.toString().toString(), "name" -> res.toList(0).name, "telefono" -> res.toList(0).telefono.toString(), "direccion" -> res.toList(0).direccion, "contacto" -> res.toList(0).contacto, "account" -> res.toList(0).account.toString())
      Ok(views.html.vendor_update(new MyDeadboltHandler, updatedRow, updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async { implicit request =>
    repo.delete(id).map { res =>
      Redirect(routes.VendorController.index)
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
        Future.successful(Ok(views.html.vendor_update(new MyDeadboltHandler, updatedRow, errorForm)))
      },
      res => {
        repo.update(
          res.id, res.name, res.telefono, res.direccion,
          res.contacto, res.account,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { _ =>
            Redirect(routes.VendorController.show(res.id))
          }
      })
  }
}

case class CreateVendorForm(name: String, telefono: Int, direccion: String, contacto: String, account: Long)

case class UpdateVendorForm(id: Long, name: String, telefono: Int, direccion: String, contacto: String, account: Long)