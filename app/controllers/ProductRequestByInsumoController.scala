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
import scala.collection.mutable.ListBuffer
import java.util.LinkedHashMap
import collection.mutable
import scala.collection.mutable.ArrayBuffer

import javax.inject._
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class ProductRequestByInsumoController @Inject() (repo: ProductRequestRepository, repoRow: RequestRowRepository,
  repoUser: UserRepository, repoInsUser: UserRepository,
  val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateProductRequestByInsumoForm] = Form {
    mapping(
      "date" -> text,
      "userId" -> longNumber,
      "status" -> text,
      "detail" -> text)(CreateProductRequestByInsumoForm.apply)(CreateProductRequestByInsumoForm.unapply)
  }

  var users = getUsersMap()

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.productRequestByInsumo_index(new MyDeadboltHandler, res))
    }
  }

  def addGet = Action { implicit request =>
    if (request.session.get("role").getOrElse("0").toLowerCase == "user") {
      users = getEmployeeNamesMap(request.session.get("userId").getOrElse("0").toLong)
    } else {
      users = getUsersMap()
    }
    Ok(views.html.productRequestByInsumo_add(new MyDeadboltHandler, newForm, users))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productRequestByInsumo_add(new MyDeadboltHandler, errorForm, users)))
      },
      res => {
        repo.createByInsumo(
          res.date, res.userId, users(res.userId.toString),
          res.status, res.detail, "insumo",
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.ProductRequestByInsumoController.show(resNew.id))
          }
      })
  }

  def getProductRequestsByEmployee(id: Long) = Action.async {
    repo.listByEmployee(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductRequestsByStorekeeper(id: Long) = Action.async {
    repo.listByStorekeeper(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductRequestsByInsumoUser(id: Long) = Action.async {
    repo.listByInsumoUser(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductRequests = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateProductRequestByInsumoForm] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "userId" -> longNumber,
      "status" -> text,
      "detail" -> text)(UpdateProductRequestByInsumoForm.apply)(UpdateProductRequestByInsumoForm.unapply)
  }

  def getChildren(productRequestId: Long): Seq[RequestRow] = {
    Await.result(repoRow.listByParent(productRequestId).map { res =>
      res
    }, 3000.millis)
  }

  // to copy
  def show(id: Long) = Action.async { implicit request =>
    val requestRows = getChildren(id)
    repo.getById(id).map { res =>
      Ok(views.html.productRequestByInsumo_show(new MyDeadboltHandler, res(0), requestRows))
    }
  }

  var updatedId: Long = 0
  // update required
  def getUpdate(id: Long) = Action.async { implicit request =>
    updatedId = id;
    repo.getById(id).map {
      case (res) =>
        val anyData = Map(
          "id" -> id.toString().toString(), "date" -> res.toList(0).date.toString(),
          "userId" -> res.toList(0).userId.toString(),
          "status" -> res.toList(0).status.toString(), "detail" -> res.toList(0).detail.toString())
        if (request.session.get("role").getOrElse("0").toLowerCase == "user") {
          users = getEmployeeNamesMap(request.session.get("userId").getOrElse("0").toLong)
        } else {
          users = getUsersMap()
        }
        Ok(views.html.productRequestByInsumo_update(new MyDeadboltHandler, updatedId, updateForm.bind(anyData), users))
    }
  }

  // update required
  def getSend(id: Long) = Action.async { implicit request =>
    repo.sendById(id).map {
      case (res) =>
        Redirect(routes.ProductRequestByInsumoController.index())
    }
  }

  // update required
  def getFinish(id: Long) = Action.async { implicit request =>
    repo.finishById(id).map {
      case (res) =>
        Redirect(routes.ProductRequestByInsumoController.index())
    }
  }

  def getEmployeeNamesMap(id: Long): Map[String, String] = {
    Await.result(repoUser.getById(id).map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach { user =>
          cache put (user.id.toString, user.name)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getUsersMap(): Map[String, String] = {
    Await.result(repoUser.listInsumoUsers().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach { user =>
          cache put (user.id.toString, user.name)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getInsumoUserNamesMap(): Map[String, String] = {
    Await.result(repoInsUser.listInsumoUsers().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach { user =>
          cache put (user.id.toString, user.name)
        }

        cache.toMap
    }, 3000.millis)
  }

  // delete required
  def delete(id: Long) = Action.async {
    val requestRows = getChildren(id)
    requestRows.foreach { req =>
      repoRow.delete(req.id)
    }
    repo.delete(id).map { res =>
      Redirect(routes.ProductRequestByInsumoController.index)
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
        Future.successful(Ok(views.html.productRequestByInsumo_update(new MyDeadboltHandler, updatedId, errorForm, Map[String, String]())))
      },
      res => {
        repo.updateByInsumo(
          res.id, res.date, res.userId, users(res.userId.toString),
          res.status, res.detail, "insumo",
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { _ =>
            Redirect(routes.ProductRequestByInsumoController.show(res.id))
          }
      })
  }
}

case class CreateProductRequestByInsumoForm(date: String, userId: Long, status: String, detail: String)

case class UpdateProductRequestByInsumoForm(id: Long, date: String, userId: Long, status: String, detail: String)