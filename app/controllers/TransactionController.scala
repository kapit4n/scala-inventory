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

  val newFormIncome: Form[CreateTransactionFormIncome] = Form {
    mapping(
      "date" -> text,
      "type_1" -> text,
      "description" -> text)(CreateTransactionFormIncome.apply)(CreateTransactionFormIncome.unapply)
  }

  val newFormOutcome: Form[CreateTransactionFormOutcome] = Form {
    mapping(
      "date" -> text,
      "type_1" -> text,
      "description" -> text,
      "receivedBy" -> longNumber,
      "autorizedBy" -> longNumber)(CreateTransactionFormOutcome.apply)(CreateTransactionFormOutcome.unapply)
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
    if (type_t == 1) { // Income
      Ok(views.html.transaction_add_1(new MyDeadboltHandler, newFormIncome))
    } else {
      Ok(views.html.transaction_add_2(new MyDeadboltHandler, newFormOutcome, users))
    }
  }

  def addIncome = Action.async { implicit request =>
    newFormIncome.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_add_1(new MyDeadboltHandler, errorForm)))
      },
      res => {
        var userId = request.session.get("userId").getOrElse("0").toLong
        var userName = request.session.get("userName").getOrElse("0").toString
        repo.createIncome(
          res.date, res.type_1, res.description,
          userId, userName,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.TransactionController.show(resNew.id))
          }
      })
  }

  def addOutcome = Action.async { implicit request =>
    newFormOutcome.bindFromRequest.fold(
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
  val updateFormIncome: Form[UpdateTransactionFormIncome] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "type_1" -> text,
      "description" -> text)(UpdateTransactionFormIncome.apply)(UpdateTransactionFormIncome.unapply)
  }

  val updateFormOutcome: Form[UpdateTransactionFormOutcome] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "type_1" -> text,
      "description" -> text,
      "receivedBy" -> longNumber,
      "autorizedBy" -> longNumber)(UpdateTransactionFormOutcome.apply)(UpdateTransactionFormOutcome.unapply)
  }

  def show(id: Long) = Action.async { implicit request =>
    val details = getTransactionDetails(id);
    repo.getById(id).map { res =>
      if (res(0).type_1 == "Income") {
        Ok(views.html.transaction_showIncome(new MyDeadboltHandler, res(0), details))
      } else {
        Ok(views.html.transaction_showOutcome(new MyDeadboltHandler, res(0), details))
      }
    }
  }

  def getUpdate(id: Long) = Action.async { implicit request =>
    users = getUsersMap()
    repo.getById(id).map { res =>
      updatedRow = res(0)
      if (updatedRow.type_1 == "Income") {
        val anyData = Map(
          "id" -> id.toString().toString(),
          "date" -> updatedRow.date.toString(),
          "type_1" -> updatedRow.type_1.toString(),
          "description" -> updatedRow.description.toString())
        Ok(views.html.transaction_updateIncome(new MyDeadboltHandler, updatedRow, updateFormIncome.bind(anyData)))
      } else {
        val anyData = Map(
          "id" -> id.toString().toString(),
          "date" -> updatedRow.date.toString(),
          "type_1" -> updatedRow.type_1.toString(),
          "description" -> updatedRow.description.toString(),
          "receivedBy" -> updatedRow.receivedBy.toString(),
          "autorizedBy" -> updatedRow.autorizedBy.toString())
        Ok(views.html.transaction_updateOutcome(new MyDeadboltHandler, updatedRow, updateFormOutcome.bind(anyData), users))
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
  def updatePostIncome = Action.async { implicit request =>
    updateFormIncome.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_updateIncome(new MyDeadboltHandler, updatedRow, errorForm)))
      },
      res => {
        repo.updateIncome(
          res.id, res.date, res.type_1,
          res.description,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.TransactionController.show(res.id))
          }
      })
  }

  def updatePostOutcome = Action.async { implicit request =>
    updateFormOutcome.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_updateOutcome(new MyDeadboltHandler, updatedRow, errorForm, users)))
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

case class CreateTransactionFormIncome(date: String, type_1: String, description: String)

case class CreateTransactionFormOutcome(date: String, type_1: String, description: String, receivedBy: Long, autorizedBy: Long)

case class UpdateTransactionFormIncome(id: Long, date: String, type_1: String, description: String)

case class UpdateTransactionFormOutcome(id: Long, date: String, type_1: String, description: String, receivedBy: Long, autorizedBy: Long)