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
import play.api.data.format.Formats._

import javax.inject._
import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler

class RequestRowCustomerController @Inject() (repo: RequestRowCustomerRepository, repoRequestRow: RequestRowRepository,
  repoProduct: ProductRepository, repoDriver: CustomerRepository, repoCustomer: CustomerRepository,
  repoUnit: MeasureRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateRequestRowCustomerForm] = Form {
    mapping(
      "requestRowId" -> longNumber,
      "productId" -> longNumber,
      "customerId" -> longNumber,
      "quantity" -> number,
      "price" -> of[Double],
      "totalPrice" -> of[Double],
      "paid" -> of[Double],
      "credit" -> of[Double],
      "status" -> text,
      "measureId" -> longNumber,
      "observation" -> text)(CreateRequestRowCustomerForm.apply)(CreateRequestRowCustomerForm.unapply)
  }

  val newDriverForm: Form[CreateRequestRowDriverForm] = Form {
    mapping(
      "requestRowId" -> longNumber,
      "productId" -> longNumber,
      "customerId" -> longNumber,
      "quantity" -> number,
      "totalPrice" -> of[Double],
      "paid" -> of[Double],
      "credit" -> of[Double],
      "status" -> text,
      "measureId" -> longNumber,
      "observation" -> text)(CreateRequestRowDriverForm.apply)(CreateRequestRowDriverForm.unapply)
  }

  var measures = getMeasureMap()
  var requestRows = getRequestRowsMap(0)
  var products = getProducts(0)
  var currentProduct: Product = _
  var customers = getCustomers()
  var drivers = getDrivers()
  var updatedRow: RequestRowCustomer = _
  var parentId: Long = _
  var requestRow: RequestRow = _
  val customerType = "customer"
  val driverType = "driver"

  def index = LanguageAction.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.requestRowCustomer_index(new MyDeadboltHandler, res))
    }
  }

  def getRequestRowObj(id: Long): RequestRow = {
    Await.result(repoRequestRow.getById(id).map { res =>
      res(0)
    }, 100.millis)
  }

  def getMeasureMap(): Map[String, String] = {
    Await.result(repoUnit.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }
        cache.toMap
    }, 3000.millis)
  }

  def addGet(requestRowId: Long) = LanguageAction { implicit request =>
    parentId = requestRowId
    requestRow = getRequestRowObj(requestRowId)
    requestRows = getRequestRowsMap(requestRowId)
    products = getProducts(requestRow.productId)
    currentProduct = getProduct(requestRow.productId)
    customers = getCustomers()
    measures = getMeasureMap()

    val anyData = Map("price" -> currentProduct.price.toString, "totalPrice" -> (requestRow.quantity * currentProduct.price).toString, "quantity" -> requestRow.quantity.toString, "paid" -> (requestRow.quantity * currentProduct.price).toString, "credit" -> "0")

    Ok(views.html.requestRowCustomer_add(new MyDeadboltHandler, parentId, searchCustomerForm, newForm.bind(anyData), requestRows,
      products, currentProduct, customers, measures))
  }

  def addDriverGet(requestRowId: Long) = LanguageAction { implicit request =>
    parentId = requestRowId
    requestRow = getRequestRowObj(requestRowId)
    requestRows = getRequestRowsMap(requestRowId)
    products = getProducts(requestRow.productId)
    drivers = getDrivers()
    measures = getMeasureMap()
    Ok(views.html.requestRowDriver_add(new MyDeadboltHandler, parentId, newDriverForm, requestRows,
      products, drivers, measures))
  }

  def add = LanguageAction.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        println(errorForm)
        Future.successful(Ok(views.html.requestRowCustomer_add(new MyDeadboltHandler, parentId, searchCustomerForm, errorForm,
          requestRows, products, currentProduct, customers, measures)))
      },
      res => {
        repo.create(
          res.requestRowId, res.productId, products(res.productId.toString()),
          res.customerId, customers(res.customerId.toString()),
          res.quantity, res.price, res.totalPrice, res.paid, res.credit, res.status, res.measureId,
          measures(res.measureId.toString()), customerType, res.observation).map { resNew =>
            repoCustomer.updateTotalDebt(res.customerId, res.credit) // in customer table
            repoRequestRow.updateRequestRow(res.requestRowId, res.paid, res.credit) // in request row table
            Redirect(routes.RequestRowCustomerController.show(resNew.id))
          }
      })
  }

  def addDriver = LanguageAction.async { implicit request =>
    newDriverForm.bindFromRequest.fold(
      errorForm => {
        println(errorForm)
        Future.successful(Ok(views.html.requestRowDriver_add(new MyDeadboltHandler, parentId, errorForm,
          requestRows, products, drivers, measures)))
      },
      res => {
        repo.create(
          res.requestRowId, res.productId, products(res.productId.toString()),
          res.customerId, drivers(res.customerId.toString()),
          0, 0, res.paid, res.paid, 0,
          res.status, res.measureId, measures(res.measureId.toString()),
          driverType, res.observation).map { resNew =>
            //repoCustomer.updateTotalDebt(res.customerId, res.credit);
            repoRequestRow.updateRequestRowDriver(res.requestRowId, res.paid, res.credit)
            Redirect(routes.RequestRowCustomerController.show(resNew.id))
          }
      })
  }

  def getRequestRowCustomers = LanguageAction.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateRequestRowCustomerForm] = Form {
    mapping(
      "id" -> longNumber,
      "requestRowId" -> longNumber,
      "productId" -> longNumber,
      "customerId" -> longNumber,
      "quantity" -> number,
      "price" -> of[Double],
      "totalPrice" -> of[Double],
      "paid" -> of[Double],
      "credit" -> of[Double],
      "status" -> text,
      "measureId" -> longNumber,
      "observation" -> text)(UpdateRequestRowCustomerForm.apply)(UpdateRequestRowCustomerForm.unapply)
  }

  val updateDriverForm: Form[UpdateRequestRowDriverForm] = Form {
    mapping(
      "id" -> longNumber,
      "requestRowId" -> longNumber,
      "productId" -> longNumber,
      "customerId" -> longNumber,
      "quantity" -> number,
      "totalPrice" -> of[Double],
      "paid" -> of[Double],
      "credit" -> of[Double],
      "status" -> text,
      "measureId" -> longNumber,
      "observation" -> text)(UpdateRequestRowDriverForm.apply)(UpdateRequestRowDriverForm.unapply)
  }

  // to copy
  def show(id: Long) = LanguageAction.async { implicit request =>
    repo.getById(id).map { res =>
      Ok(views.html.requestRowCustomer_show(new MyDeadboltHandler, res(0)))
    }
  }

  // update required
  def getUpdate(id: Long) = LanguageAction.async { implicit request =>
    repo.getById(id).map {
      case (res) =>
        updatedRow = res(0)
        val anyData = Map("id" -> id.toString().toString(), "requestRowId" -> updatedRow.requestRowId.toString(),
          "productId" -> updatedRow.productId.toString(), "customerId" -> updatedRow.customerId.toString(),
          "quantity" -> updatedRow.quantity.toString(), "price" -> updatedRow.price.toString(),
          "totalPrice" -> updatedRow.totalPrice.toString(), "paid" -> updatedRow.paid.toString(),
          "credit" -> updatedRow.credit.toString(), "status" -> updatedRow.status.toString(),
          "measureId" -> updatedRow.measureId.toString(), "observation" -> updatedRow.observation)
        requestRows = getRequestRowsMap(updatedRow.requestRowId)
        requestRow = getRequestRowObj(updatedRow.requestRowId)
        products = getProducts(requestRow.productId)
        customers = getCustomerById(updatedRow.productId)

        Ok(views.html.requestRowCustomer_update(new MyDeadboltHandler, updatedRow,
          updateForm.bind(anyData), requestRows, products, customers, measures))

    }
  }

  // update required
  def getDriverUpdate(id: Long) = LanguageAction.async { implicit request =>
    repo.getById(id).map {
      case (res) =>
        updatedRow = res(0)
        val anyData = Map("id" -> id.toString().toString(), "requestRowId" -> updatedRow.requestRowId.toString(),
          "productId" -> updatedRow.productId.toString(), "customerId" -> updatedRow.customerId.toString(),
          "quantity" -> updatedRow.quantity.toString(),
          "totalPrice" -> updatedRow.price.toString(), "paid" -> updatedRow.price.toString(),
          "credit" -> updatedRow.price.toString(), "status" -> updatedRow.status.toString(),
          "measureId" -> updatedRow.measureId.toString(), "observation" -> updatedRow.observation)
        requestRows = getRequestRowsMap(updatedRow.requestRowId)
        requestRow = getRequestRowObj(updatedRow.requestRowId)
        products = getProducts(requestRow.productId)
        drivers = getDriverById(updatedRow.productId)

        Ok(views.html.requestRowDriver_update(new MyDeadboltHandler, updatedRow,
          updateDriverForm.bind(anyData), requestRows, products, drivers, measures))

    }
  }

  def getRequestRowsMap(requestRowId: Long): Map[String, String] = {
    Await.result(repoRequestRow.getById(requestRowId).map { res =>
      val cache = collection.mutable.Map[String, String]()
      res.foreach { requestRow =>
        cache put (requestRow.id.toString, requestRow.id.toString() + ": " + requestRow.productName)
      }
      cache.toMap
    }, 3000.millis)
  }

  def getProducts(id: Long): Map[String, String] = {
    Await.result(repoProduct.getById(id).map { res1 =>
      val cache = collection.mutable.Map[String, String]()
      res1.foreach { product =>
        cache put (product.id.toString(), product.name)
      }
      cache.toMap
    }, 3000.millis)
  }

  def getProduct(id: Long): Product = {
    Await.result(repoProduct.getById(id).map { res1 =>
      res1(0)
    }, 3000.millis)
  }

  def getDrivers(id: Long): Map[String, String] = {
    Await.result(repoProduct.getById(id).map { res1 =>
      val cache = collection.mutable.Map[String, String]()
      res1.foreach { product =>
        cache put (product.id.toString(), product.name)
      }
      cache.toMap
    }, 3000.millis)
  }

  def getCustomers(): Map[String, String] = {
    Await.result(repoCustomer.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getDrivers(): Map[String, String] = {
    Await.result(repoCustomer.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getCustomerById(productId: Long): Map[String, String] = {
    Await.result(repoCustomer.getById(productId).map {
      case res1 =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach { product =>
          cache put (product.id.toString, product.name)
        }
        cache.toMap
    }, 3000.millis)
  }

  def getDriverById(driverId: Long): Map[String, String] = {
    Await.result(repoDriver.getById(driverId).map {
      case res1 =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach { driver =>
          cache put (driver.id.toString, driver.name)
        }
        cache.toMap
    }, 3000.millis)
  }

  // update required
  def getAccept(id: Long) = LanguageAction.async {
    repo.acceptById(id).map {
      case (res) =>
        repoProduct.updateAmount(res(0).productId, -res(0).quantity);
        Redirect(routes.RequestRowController.show(res(0).requestRowId))
    }
  }

  // update required
  def getSend(id: Long) = LanguageAction.async {
    repo.sendById(id).map {
      case (res) =>
        Redirect(routes.RequestRowController.show(res(0).requestRowId))
    }
  }

  // update required
  def getFinish(id: Long) = LanguageAction.async {
    repo.finishById(id).map {
      case (res) =>
        Redirect(routes.RequestRowController.show(res(0).requestRowId))
    }
  }

  def getParentId(id: Long): Long = {
    Await.result(repo.getById(id).map { res =>
      res(0).requestRowId
    }, 3000.millis)
  }

  // delete required
  def delete(id: Long) = LanguageAction.async {
    var requestRowId = getParentId(id)
    repo.delete(id).map { res =>
      Redirect(routes.RequestRowController.show(requestRowId)) // review this to go to the requestRow view
    }
  }

  // to copy
  def getById(id: Long) = LanguageAction.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // to copy
  def requestRowCustomersByCustomer(id: Long) = LanguageAction.async {
    repo.requestRowCustomersByCustomer(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getByRequestRow(id: Long) = LanguageAction.async {
    repo.requestRowCustomersByRequestRow(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def updatePost = LanguageAction.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRowCustomer_update(new MyDeadboltHandler, updatedRow,
          errorForm, requestRows, products, customers, measures)))
      },
      res => {
        repo.update(res.id, res.requestRowId, res.productId, products(res.productId.toString), res.customerId,
          customers(res.customerId.toString), res.quantity, res.price, res.totalPrice, res.paid,
          res.credit, res.status, res.measureId, measures(res.measureId.toString()),
          customerType, res.observation).map { _ =>
            repoRequestRow.updateRequestRow(res.requestRowId, res.paid - updatedRow.paid, res.credit - updatedRow.credit) // in request row table
            Redirect(routes.RequestRowCustomerController.show(res.id))
          }
      })
  }

  def updateDriverPost = LanguageAction.async { implicit request =>
    updateDriverForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRowDriver_update(new MyDeadboltHandler, updatedRow,
          errorForm, requestRows, products, drivers, measures)))
      },
      res => {
        repo.update(res.id, res.requestRowId, res.productId, products(res.productId.toString), res.customerId,
          drivers(res.customerId.toString), res.quantity, 0, res.totalPrice, res.paid,
          res.credit, res.status, res.measureId, measures(res.measureId.toString()),
          driverType, res.observation).map { _ =>
            repoRequestRow.updateRequestRowDriver(res.requestRowId, res.paid - updatedRow.paid, res.credit - updatedRow.credit) // in request row table
            Redirect(routes.RequestRowCustomerController.show(res.id))
          }
      })
  }

  val searchCustomerForm: Form[SearchCustomerForm] = Form {
    mapping(
      "search" -> text)(SearchCustomerForm.apply)(SearchCustomerForm.unapply)
  }


  def searchCustomerPost = LanguageAction.async { implicit request =>
    searchCustomerForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.requestRowCustomer_add(new MyDeadboltHandler, parentId, searchCustomerForm,
          newForm, requestRows, products, currentProduct, customers, measures)))
      },
      res => {
        repoCustomer.searchCustomer(res.search).map { resCustomers =>
          val cache = collection.mutable.Map[String, String]()
          resCustomers.map { customer =>
            cache put (customer.id.toString(), customer.account.toString + ": " + customer.name.toString)
          }
          customers = cache.toMap
          Ok(views.html.requestRowCustomer_add(new MyDeadboltHandler, parentId, searchCustomerForm, newForm, requestRows, products, currentProduct, customers, measures))
        }
      })
  }
}

case class CreateRequestRowCustomerForm(requestRowId: Long, productId: Long,
  customerId: Long, quantity: Int,
  price: Double, totalPrice: Double, paid: Double, credit: Double, status: String,
  measureId: Long, observation: String)

case class CreateRequestRowDriverForm(requestRowId: Long, productId: Long,
  customerId: Long, quantity: Int,
  totalPrice: Double, paid: Double, credit: Double, status: String,
  measureId: Long, observation: String)

case class UpdateRequestRowCustomerForm(id: Long, requestRowId: Long, productId: Long,
  customerId: Long, quantity: Int,
  price: Double, totalPrice: Double, paid: Double, credit: Double, status: String,
  measureId: Long, observation: String)

case class UpdateRequestRowDriverForm(id: Long, requestRowId: Long, productId: Long,
  customerId: Long, quantity: Int,
  totalPrice: Double, paid: Double, credit: Double, status: String,
  measureId: Long, observation: String)
