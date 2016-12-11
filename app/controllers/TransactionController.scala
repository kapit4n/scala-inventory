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

class TransactionController @Inject() (
  repo: TransactionRepository, repoDetail: TransactionDetailRepository,
  repoVete: UserRepository, repoSto: UserRepository,
  val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newFormIngreso: Form[CreateTransactionFormIngreso] = Form {
    mapping(
      "date" -> text,
      "type_1" -> text,
      "description" -> text)(CreateTransactionFormIngreso.apply)(CreateTransactionFormIngreso.unapply)
  }

  val newFormEgreso: Form[CreateTransactionFormEgreso] = Form {
    mapping(
      "date" -> text,
      "type_1" -> text,
      "description" -> text,
      "receivedBy" -> longNumber,
      "autorizedBy" -> longNumber)(CreateTransactionFormEgreso.apply)(CreateTransactionFormEgreso.unapply)
  }

  var users = getUsersMap()
  var updatedRow: Transaction = _

  def getUsersMap(): Map[String, String] = {
    Await.result(repoVete.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getTransactionDetails(id: Long): Seq[TransactionDetail] = {
    Await.result(repoDetail.listByTransaction(id).map {
      case (res) =>
        res
    }, 1000.millis)
  }

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.transaction_index(new MyDeadboltHandler, res))
    }
  }

  def addGet(type_t: Long) = Action { implicit request =>
    users = getUsersMap()
    if (type_t == 1) { // Ingreso
      Ok(views.html.transaction_add_1(new MyDeadboltHandler, newFormIngreso))
    } else {
      Ok(views.html.transaction_add_2(new MyDeadboltHandler, newFormEgreso, users))
    }
  }

  def addIngreso = Action.async { implicit request =>
    newFormIngreso.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_add_1(new MyDeadboltHandler, errorForm)))
      },
      res => {
        var userId = request.session.get("userId").getOrElse("0").toLong
        var userName = request.session.get("userName").getOrElse("0").toString
        repo.createIngreso(
          res.date, res.type_1, res.description,
          userId, userName,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.TransactionController.show(resNew.id))
          }
      })
  }

  def addEgreso = Action.async { implicit request =>
    newFormEgreso.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_add_2(new MyDeadboltHandler, errorForm, users)))
      },
      res => {
        var userId = request.session.get("userId").getOrElse("0").toLong
        var userName = request.session.get("userName").getOrElse("0").toString
        repo.create(
          res.date, res.type_1, res.description,
          userId, userName,
          res.receivedBy, users(res.receivedBy.toString),
          res.autorizedBy, users(res.autorizedBy.toString),
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.TransactionController.show(resNew.id))
          }
      })
  }

  def getTransactions = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateFormIngreso: Form[UpdateTransactionFormIngreso] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "type_1" -> text,
      "description" -> text)(UpdateTransactionFormIngreso.apply)(UpdateTransactionFormIngreso.unapply)
  }

  val updateFormEgreso: Form[UpdateTransactionFormEgreso] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "type_1" -> text,
      "description" -> text,
      "receivedBy" -> longNumber,
      "autorizedBy" -> longNumber)(UpdateTransactionFormEgreso.apply)(UpdateTransactionFormEgreso.unapply)
  }

  def show(id: Long) = Action.async { implicit request =>
    val details = getTransactionDetails(id);
    repo.getById(id).map { res =>
      if (res(0).type_1 == "Ingreso") {
        Ok(views.html.transaction_showIngreso(new MyDeadboltHandler, res(0), details))
      } else {
        Ok(views.html.transaction_showEgreso(new MyDeadboltHandler, res(0), details))
      }
    }
  }

  def getUpdate(id: Long) = Action.async { implicit request =>
    users = getUsersMap()
    repo.getById(id).map { res =>
      updatedRow = res(0)
      if (updatedRow.type_1 == "Ingreso") {
        val anyData = Map(
          "id" -> id.toString().toString(),
          "date" -> updatedRow.date.toString(),
          "type_1" -> updatedRow.type_1.toString(),
          "description" -> updatedRow.description.toString())
        Ok(views.html.transaction_updateIngreso(new MyDeadboltHandler, updatedRow, updateFormIngreso.bind(anyData)))
      } else {
        val anyData = Map(
          "id" -> id.toString().toString(),
          "date" -> updatedRow.date.toString(),
          "type_1" -> updatedRow.type_1.toString(),
          "description" -> updatedRow.description.toString(),
          "receivedBy" -> updatedRow.receivedBy.toString(),
          "autorizedBy" -> updatedRow.autorizedBy.toString())
        Ok(views.html.transaction_updateEgreso(new MyDeadboltHandler, updatedRow, updateFormEgreso.bind(anyData), users))
      }
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Redirect(routes.TransactionController.index())
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  def updatePostIngreso = Action.async { implicit request =>
    updateFormIngreso.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_updateIngreso(new MyDeadboltHandler, updatedRow, errorForm)))
      },
      res => {
        repo.updateIngreso(
          res.id, res.date, res.type_1,
          res.description,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.TransactionController.show(res.id))
          }
      })
  }

  def updatePostEgreso = Action.async { implicit request =>
    updateFormEgreso.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_updateEgreso(new MyDeadboltHandler, updatedRow, errorForm, users)))
      },
      res => {
        repo.update(
          res.id, res.date, res.type_1,
          res.description, res.receivedBy,
          users(res.receivedBy.toString),
          res.autorizedBy, users(res.autorizedBy.toString),
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.TransactionController.show(res.id))
          }
      })
  }

}

case class CreateTransactionFormIngreso(date: String, type_1: String, description: String)

case class CreateTransactionFormEgreso(date: String, type_1: String, description: String, receivedBy: Long, autorizedBy: Long)

case class UpdateTransactionFormIngreso(id: Long, date: String, type_1: String, description: String)

case class UpdateTransactionFormEgreso(id: Long, date: String, type_1: String, description: String, receivedBy: Long, autorizedBy: Long)