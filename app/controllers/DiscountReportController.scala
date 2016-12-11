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
import it.innove.play.pdf.PdfGenerator
import play.api.data.format.Formats._
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class DiscountReportController @Inject() (repo: DiscountReportRepository, repoProd: CustomerRepository,
  repoSto: UserRepository, repoDiscDetail: DiscountDetailRepository, repoRequestRows: RequestRowRepository,
  repoRequestRowCustomers: RequestRowCustomerRepository,
  repoSetting: SettingRepository,
  val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateDiscountReportForm] = Form {
    mapping(
      "startDate" -> text,
      "endDate" -> text,
      "status" -> text)(CreateDiscountReportForm.apply)(CreateDiscountReportForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.discountReport_index(new MyDeadboltHandler, res))
    }
  }

  def generarReport(id: Long) = Action {
    val productRequestRowsByProduct = getProductByTotalDebt()
    repoDiscDetail.generarReport(productRequestRowsByProduct, id)
    Redirect(routes.DiscountReportController.show(id))
  }

  var customeresNames = getCustomeresNamesMap()

  def addGet = Action { implicit request =>
    customeresNames = getCustomeresNamesMap()
    Ok(views.html.discountReport_add(new MyDeadboltHandler, newForm, customeresNames))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.discountReport_add(new MyDeadboltHandler, newForm, customeresNames)))
      },
      res => {
        repo.create(res.startDate, res.endDate, res.status).map { resNew =>
          Redirect(routes.DiscountReportController.show(resNew.id))
        }
      })
  }

  def getDiscountReports = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateDiscountReportForm] = Form {
    mapping(
      "id" -> longNumber,
      "startDate" -> text,
      "endDate" -> text,
      "status" -> text,
      "total" -> of[Double])(UpdateDiscountReportForm.apply)(UpdateDiscountReportForm.unapply)
  }

  def getChildren(id: Long): Seq[DiscountDetail] = {
    Await.result(repoDiscDetail.listByParent(id).map { res =>
      res
    }, 3000.millis)
  }

  // to copy
  def show(id: Long) = Action.async { implicit request =>
    val children = getChildren(id)
    repo.getById(id).map { res =>
      Ok(views.html.discountReport_show(new MyDeadboltHandler, res(0), children))
    }
  }

  def getSettingInfo(): Setting = {
    Await.result(repoSetting.getFirst().map { res => res(0) }, 1000.millis)
  }

  def show_pdf(id: Long) = Action {
    val generator = new PdfGenerator
    val discountReport = getDiscountReportById(id)
    val values = getDiscountDetailList(id)
    val setting = getSettingInfo()

    Ok(generator.toBytes(views.html.discountReport_show_pdf(values, discountReport.startDate, discountReport.endDate, discountReport.total.toString(), setting), "http://localhost:9000/")).as("application/pdf")
  }

  var updatedRow: DiscountReport = _

  // update required
  def getUpdate(id: Long) = Action.async { implicit request =>
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString(), "startDate" -> res.toList(0).startDate.toString(), "endDate" -> res.toList(0).endDate.toString(), "status" -> res.toList(0).status.toString(), "total" -> res.toList(0).total.toString())
      val customeresMap = getCustomeresNamesMap()
      updatedRow = res(0)
      Ok(views.html.discountReport_update(new MyDeadboltHandler, res(0), updateForm.bind(anyData), customeresMap))
    }
  }

  def getCustomeresNamesMap(): Map[String, String] = {
    Await.result(repoProd.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }
        cache.toMap
    }, 3000.millis)
  }

  def getDiscountReportById(id: Long): DiscountReport = {
    Await.result(repo.getById(id).map {
      case (res1) =>
        res1(0)
    }, 500.millis)
  }

  def getProductByTotalDebt(): Seq[Customer] = {
    Await.result(repoProd.listByTotalDebt().map {
      case (res1) =>
        res1
    }, 3000.millis)
  }

  def getDiscountDetailList(id: Long): Seq[DiscountDetail] = {
    Await.result(repoDiscDetail.listByReport(id).map {
      case (res1) =>
        res1
    }, 3000.millis)
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Redirect(routes.DiscountReportController.index)
    }
  }

  /* Update all report details to dinalized
  * Update all customers totalDebt with -discount
  */
  def finalizeReport(id: Long) = Action.async {
    val discountDetails = getDiscountDetailList(id)
    discountDetails.foreach {
      case (discountDetail) =>
        // maybe I will need to update this to repoRequestRowCustomers
        repoRequestRowCustomers.updatePaid(discountDetail.requestRow, discountDetail.discount);
        repoProd.updateTotalDebt(discountDetail.customerId, -discountDetail.discount);
    }

    repo.finalizeById(id).map {
      case (res) =>
        Redirect(routes.DiscountReportController.index)
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
        Future.successful(Ok(views.html.discountReport_update(new MyDeadboltHandler, updatedRow, errorForm, Map[String, String]())))
      },
      res => {
        repo.update(res.id, res.startDate, res.endDate, res.status, res.total).map { _ =>
          Redirect(routes.DiscountReportController.show(res.id))
        }
      })
  }
}

case class CreateDiscountReportForm(startDate: String, endDate: String, status: String)

case class UpdateDiscountReportForm(id: Long, startDate: String, endDate: String, status: String, total: Double)