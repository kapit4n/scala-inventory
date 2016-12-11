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

class ProductInvController @Inject() (repo: ProductInvRepository, repoProduct: ProductRepository,
  repoMeasure: MeasureRepository,
  repoProvee: VendorRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateProductInvForm] = Form {
    mapping(
      "productId" -> longNumber,
      "vendorId" -> longNumber,
      "measureId" -> longNumber,
      "amount" -> number)(CreateProductInvForm.apply)(CreateProductInvForm.unapply)
  }

  // update required
  val updateForm: Form[UpdateProductInvForm] = Form {
    mapping(
      "id" -> longNumber,
      "productId" -> longNumber,
      "vendorId" -> longNumber,
      "measureId" -> longNumber,
      "amount" -> number,
      "amountLeft" -> number)(UpdateProductInvForm.apply)(UpdateProductInvForm.unapply)
  }

  var productMap = getProductMapById(0)
  var vendorMap = getVendorMap()
  var measureMap = getMeasureMap(0)
  var productId: Long = 0
  var updatedRow: ProductInv = _

  def index = Action.async { implicit request =>
    repo.list().map { res =>
      Ok(views.html.productInv_index(new MyDeadboltHandler, res))
    }
  }

  def getProductMeasureId(id: Long): Long = {
    Await.result(repoProduct.getById(id).map(res => res(0).measureId), 3000.millis)
  }

  def addGet(productId: Long) = Action { implicit request =>
    this.productId = productId
    productMap = getProductMapById(productId)
    vendorMap = getVendorMap()
    var productMeasureId = getProductMeasureId(productId)
    measureMap = getMeasureMap(productMeasureId)
    Ok(views.html.productInv_add(new MyDeadboltHandler, productId, newForm, productMap, vendorMap, measureMap))
  }

  def getProductInvs = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // to copy
  def show(id: Long) = Action.async { implicit request =>
    repo.getById(id).map { res =>
      Ok(views.html.productInv_show(new MyDeadboltHandler, res(0)))
    }
  }

  // update required
  def getUpdate(id: Long) = Action.async { implicit request =>
    repo.getById(id).map {
      case (res) =>
        updatedRow = res(0)
        val anyData = Map(
          "id" -> id.toString().toString(),
          "productId" -> updatedRow.productId.toString(),
          "vendorId" -> updatedRow.vendorId.toString(),
          "measureId" -> updatedRow.vendorId.toString(),
          "amount" -> updatedRow.amount.toString(),
          "amountLeft" -> updatedRow.amountLeft.toString())
        productMap = getProductMapById(updatedRow.productId)
        vendorMap = getVendorMap()
        var productMeasureId = getProductMeasureId(updatedRow.productId)
        measureMap = getMeasureMap(productMeasureId)
        Ok(views.html.productInv_update(new MyDeadboltHandler, updatedRow, updateForm.bind(anyData), productMap, vendorMap, measureMap))
    }
  }

  def getProductMapById(product: Long): Map[String, String] = {
    Await.result(repoProduct.getListNamesById(product).map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getVendorMap(): Map[String, String] = {
    Await.result(repoProvee.getListNames().map {
      case (res1) =>
        val cache = collection.mutable.Map[String, String]()
        res1.foreach {
          case (key: Long, value: String) =>
            cache put (key.toString(), value)
        }

        cache.toMap
    }, 3000.millis)
  }

  def getMeasureMap(measureId: Long): Map[String, String] = {
    Await.result(repoMeasure.getById(measureId).map { measures =>
      val cache = collection.mutable.Map[String, String]()
      measures.foreach { measure =>
        cache put (measure.id.toString, measure.name)
      }
      cache.toMap
    }, 3000.millis)
  }

  def getParentId(id: Long): Long = {
    Await.result(repo.getById(id).map { res =>
      res(0).productId
    }, 200.millis)
  }

  def getAmountLeft(id: Long): Int = {
    Await.result(repo.getById(id).map { res =>
      res(0).amountLeft
    }, 1000.millis)
  }

  // delete required
  def delete(id: Long) = Action.async {
    val parentId = getParentId(id)
    val amountLeft = getAmountLeft(id)
    repo.delete(id).map { res =>
      repoProduct.updateInventary(parentId, -amountLeft);
      Redirect(routes.ProductController.show(parentId))
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productInv_add(new MyDeadboltHandler, productId, errorForm, productMap, vendorMap, measureMap)))
      },
      res => {
        repo.create(
          res.productId, productMap(res.productId.toString),
          res.vendorId, vendorMap(res.vendorId.toString),
          res.measureId, measureMap(res.measureId.toString),
          res.amount, res.amount).map { resNew =>
            repoProduct.updateAmount(res.productId, res.amount)
            Redirect(routes.ProductInvController.show(resNew.id))
          }
      })
  }

  // update required
  def updatePost = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productInv_update(new MyDeadboltHandler, updatedRow, updateForm, productMap, vendorMap, measureMap)))
      },
      res => {
        val oldAmountLeft = getAmountLeft(res.id)
        repo.update(
          res.id,
          res.productId, productMap(res.productId.toString),
          res.vendorId, vendorMap(res.vendorId.toString),
          res.measureId, measureMap(res.measureId.toString),
          res.amount, res.amountLeft).map { _ =>
            repoProduct.updateInventary(res.productId, res.amountLeft - oldAmountLeft);
            Redirect(routes.ProductInvController.show(res.id))
          }
      })
  }

}

case class CreateProductInvForm(productId: Long, vendorId: Long, measureId: Long, amount: Int)

case class UpdateProductInvForm(id: Long, productId: Long, vendorId: Long, measureId: Long, amount: Int, amountLeft: Int)