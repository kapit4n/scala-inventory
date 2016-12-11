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
import play.api.data.format.Formats._
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class CustomerController @Inject() (
  repo: CustomerRepository, repoRequests: RequestRowCustomerRepository,
  repoModule: ModuleRepository, repoCustomer: CustomerRepository, repoDiscount: DiscountDetailRepository,
  val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  var modules = getModuleNamesMap()
  var interval = 30
  var updatedRow: Customer = _

  def getModuleNamesMap(): Map[String, String] = {
    Await.result(repoModule.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }
        cache.toMap
    }, 3000.millis)
  }

  def getTotal(): Int = {
    Await.result(repo.getTotal().map {
      case (res1) =>
        res1
    }, 3000.millis)
  }

  def searchCustomer(search: String): Seq[Customer] = {
    Await.result(repo.searchCustomer(search).map { res =>
      res
    }, 1000.millis)
  }

  val newForm: Form[CreateCustomerForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "telefono" -> number.verifying(min(0), max(9999999)),
      "direccion" -> nonEmptyText,
      "account" -> text,
      "module" -> longNumber)(CreateCustomerForm.apply)(CreateCustomerForm.unapply)
  }

  def addGet() = Action.async { implicit request =>
    repo.list(0 * interval, interval).map { res =>
      modules = getModuleNamesMap()
      Ok(views.html.customer_add(new MyDeadboltHandler, newForm, modules))
    }
  }

  var total: Int = _
  var currentPage: Int = _
  var customers: Seq[Customer] = Seq[Customer]()

  def index(start: Int) = Action.async { implicit request =>
    if (start == 0) {
      Future(Ok(views.html.customer_index(new MyDeadboltHandler, newForm, searchForm, customers, total, currentPage, interval)))
    } else {
      repo.list((start - 1) * interval, interval).map { res =>
        customers = res
        total = getTotal()
        currentPage = start
        Ok(views.html.customer_index(new MyDeadboltHandler, newForm, searchForm, customers, total, currentPage, interval))
      }
    }
  }

  var companys: Seq[Company] = _

  def index_company() = Action.async { implicit request =>
    repo.listCompany().map { res =>
      companys = res
      Ok(views.html.company_index(new MyDeadboltHandler, companys))
    }
  }

  def searchCustomerPost = Action.async { implicit request =>
    var total = getTotal()
    var currentPage = 1
    searchForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.customer_index(new MyDeadboltHandler, newForm, searchForm, customers, total, currentPage, interval)))
      },
      res => {
        customers = searchCustomer(res.search)
        modules = getModuleNamesMap()
        var total = getTotal()
        var currentPage = 1
        Future(Ok(views.html.customer_index(new MyDeadboltHandler, newForm, searchForm, customers, total, currentPage, interval)))
      })
  }

  def index_pdf = Action {
    val generator = new PdfGenerator
    Ok(generator.toBytes(views.html.reporte_customeres(), "http://localhost:9000/")).as("application/pdf")
  }

  def getModuleById(id: Long): Module = {
    Await.result(repoModule.getById(id).map(res => res(0)), 2000.millis)
  }

  def add = Action.async { implicit request =>
    var total = getTotal()
    var currentPage = 1
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.customer_add(new MyDeadboltHandler, errorForm, modules)))
      },
      res => {
        val module = getModuleById(res.module)
        repo.create(res.name, res.carnet, res.telefono, res.direccion,
          res.account, res.module, module.name,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { resNew =>
            Redirect(routes.CustomerController.show(resNew.id))
          }
      })
  }

  def getCustomersByModule(id: Long) = Action.async {
    repo.list(0, interval).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getCustomeres(page: Int) = Action.async {
    repo.list((page - 1) * interval, interval).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getCustomeresReport = Action.async {
    repo.list(0, interval).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateCustomerForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "carnet" -> number,
      "telefono" -> number,
      "direccion" -> nonEmptyText,
      "account" -> text,
      "module" -> longNumber,
      "totalDebt" -> of[Double],
      "numberPayment" -> number)(UpdateCustomerForm.apply)(UpdateCustomerForm.unapply)
  }

  val searchForm: Form[SearchCustomerForm] = Form {
    mapping(
      "search" -> text)(SearchCustomerForm.apply)(SearchCustomerForm.unapply)
  }

  def getRequests(id: Long): Seq[RequestRowCustomer] = {
    Await.result(repoRequests.listByCustomer(id).map { res =>
      res
    }, 1000.millis)
  }

  def getDiscounts(id: Long): Seq[DiscountDetail] = {
    Await.result(repoDiscount.listByCustomer(id).map { res =>
      res
    }, 1000.millis)
  }

  def getCustomersByAsso(id: Long): Seq[Customer] = {
    Await.result(repoCustomer.listByCompany(id).map { res =>
      res
    }, 1000.millis)
  }

  // to copy
  def show(id: Long) = Action.async { implicit request =>
    val requests = getRequests(id)
    val discounts = getDiscounts(id)
    repo.getById(id).map { res =>
      Ok(views.html.customer_show(new MyDeadboltHandler, res(0), requests, discounts))
    }
  }

  def showCompany(id: Long) = Action.async { implicit request =>
    val customeresByAsso = getCustomersByAsso(id)
    repo.getCompanyById(id).map { res =>
      Ok(views.html.company_show(new MyDeadboltHandler, res(0), customeresByAsso))
    }
  }

  // update required
  def getUpdate(id: Long) = Action.async { implicit request =>
    modules = getModuleNamesMap()
    repo.getById(id).map { res =>
      updatedRow = res(0)
      val anyData = Map(
        "id" -> id.toString().toString(),
        "name" -> updatedRow.name,
        "carnet" -> updatedRow.carnet.toString(),
        "telefono" -> updatedRow.telefono.toString(),
        "direccion" -> updatedRow.direccion,
        "account" -> updatedRow.account.toString(),
        "module" -> updatedRow.module.toString(),
        "totalDebt" -> updatedRow.totalDebt.toString(),
        "numberPayment" -> updatedRow.numberPayment.toString())
      Ok(views.html.customer_update(new MyDeadboltHandler, updatedRow, updateForm.bind(anyData), modules))
    }
  }

  // delete required
  def delete(id: Long) = Action.async { implicit request =>
    //var total = getTotal()
    //var currentPage = 1
    repo.delete(id).map { res =>
      Redirect(routes.CustomerController.index(1))
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getCompanyById(id: Long) = Action.async {
    repo.getCompanyById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  def updatePost = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.customer_update(new MyDeadboltHandler, updatedRow, errorForm, modules)))
      },
      res => {
        repo.update(
          res.id, res.name, res.carnet, res.telefono,
          res.direccion, res.account, res.module,
          modules(res.module.toString()), "Company Name",
          res.totalDebt, res.numberPayment,
          request.session.get("userId").get.toLong,
          request.session.get("userName").get.toString).map { _ =>
            Redirect(routes.CustomerController.show(res.id))
          }
      })
  }
}

case class CreateCustomerForm(
  name: String, carnet: Int, telefono: Int,
  direccion: String, account: String, module: Long)

case class UpdateCustomerForm(
  id: Long, name: String, carnet: Int, telefono: Int,
  direccion: String, account: String, module: Long,
  totalDebt: Double, numberPayment: Int)

case class SearchCustomerForm(search: String)
