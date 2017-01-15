package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.RequestRowCustomer

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class RequestRowCustomerRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, repoProduct: ProductRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RequestRowCustomerTable(tag: Tag) extends Table[RequestRowCustomer](tag, "requestRowCustomer") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def requestRowId = column[Long]("requestRowId")
    def productId = column[Long]("productId")
    def productName = column[String]("productName")
    def customerId = column[Long]("customerId") // customer, driver
    def customerName = column[String]("customerName")
    def quantity = column[Int]("quantity") // amount of products
    def price = column[Double]("price") // unit price of the product
    def totalPrice = column[Double]("totalPrice") // price * quantity
    def paid = column[Double]("paid") // money already paid at the moment
    def credit = column[Double]("credit") // the value that need to pay with discount on another baucher
    def status = column[String]("status") // completed, credit, 
    def measureId = column[Long]("measureId") // measure of the paid
    def measureName = column[String]("measureName")
    def type_1 = column[String]("type") // customer, driver
    def observation = column[String]("payType") // observations of the pay
    def * = (id, requestRowId, productId, productName, customerId, customerName,
      quantity, price, totalPrice, paid, credit, status, measureId, measureName, type_1, observation) <>
      ((RequestRowCustomer.apply _).tupled, RequestRowCustomer.unapply)
  }

  private val tableQ = TableQuery[RequestRowCustomerTable]

  def create(requestRowId: Long, productId: Long, productName: String, customerId: Long, customerName: String,
    quantity: Int, price: Double, totalPrice: Double, paid: Double, credit: Double,
    status: String, measureId: Long, measureName: String, type_1: String, observation: String): Future[RequestRowCustomer] = db.run {
    (tableQ.map(p => (p.requestRowId, p.productId, p.productName, p.customerId, p.customerName, p.quantity,
      p.price, p.totalPrice, p.paid, p.credit, p.status, p.measureId, p.measureName, p.type_1, p.observation))
      returning tableQ.map(_.id)
      into ((nameAge, id) => RequestRowCustomer(
        id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6, nameAge._7,
        nameAge._8, nameAge._9, nameAge._10, nameAge._11, nameAge._12, nameAge._13, nameAge._14,
        nameAge._15))) += (requestRowId, productId, productName, customerId, customerName, quantity,
        price, totalPrice, paid, credit, status, measureId, measureName, type_1, observation)
  }

  def list(): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.result
  }

  def listByQuantity(): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(_.quantity > 0).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // to cpy
  def requestRowCustomersByCustomer(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(_.customerId === id).result
  }

  // to cpy
  def requestRowCustomersByRequestRow(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(_.requestRowId === id).result
  }

  def listByParent(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(_.requestRowId === id).result
  }

  def listDriversByParent(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(x => x.requestRowId === id && x.type_1 === "driver").result
  }

  def listByCustomer(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    tableQ.filter(_.customerId === id).result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.customerId.toString())).result
  }

  // update required to copy, add totalPrice, credit and observations
  def update(id: Long, requestRowId: Long, productId: Long, productName: String, customerId: Long, customerName: String,
    quantity: Int, price: Double, totalPrice: Double, paid: Double, credit: Double, status: String, measureId: Long,
    measureName: String, type_1: String, observation: String): Future[Seq[RequestRowCustomer]] = db.run {
    db.run(tableQ.filter(_.id === id).map(x => (
      x.requestRowId, x.productId, x.productName, x.customerId, x.customerName,
      x.quantity, x.price, x.totalPrice, x.paid, x.credit, x.status, x.measureId,
      x.measureName, x.type_1, x.observation))
      .update(requestRowId, productId, productName, customerId, customerName, quantity, price,
        totalPrice, paid, credit, status, measureId, measureName, type_1, observation))
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def sendById(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def acceptById(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def updatePaid(id: Long, monto: Double): Future[Seq[RequestRowCustomer]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.paid
    getById(id).map { row =>
      db.run(q.update(row(0).paid + monto))
    }
    tableQ.filter(_.id === id).result
  }

  // Update the status to finalizado status
  def finishById(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[RequestRowCustomer]] = db.run {
    getById(id).map { row =>
      repoProduct.updateInventary(row(0).productId, row(0).quantity)
      val q = tableQ.filter(_.id === id)
      val action = q.delete
      val affectedRowsCount: Future[Int] = db.run(action)

    }
    tableQ.result
  }
}
