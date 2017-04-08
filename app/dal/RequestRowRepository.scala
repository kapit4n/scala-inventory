package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.RequestRow
import models.Product

import scala.concurrent.{ Future, ExecutionContext, Await }
import scala.concurrent.duration._

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class RequestRowRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,
  repoInsum: ProductRepository,
  repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RequestRowTable(tag: Tag) extends Table[RequestRow](tag, "requestRow") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def requestId = column[Long]("requestId")
    def productId = column[Long]("productId")
    def productName = column[String]("productName")
    def quantity = column[Int]("quantity")
    def price = column[Double]("price")
    def totalPrice = column[Double]("totalPrice")
    def paid = column[Double]("paid")
    def credit = column[Double]("credit")
    def paidDriver = column[Double]("paidDriver")
    def creditDriver = column[Double]("creditDriver")
    def status = column[String]("status")
    def measureId = column[Long]("measureId")
    def measureName = column[String]("measureName")
    def * = (
      id, requestId, productId, productName, quantity,
      price, totalPrice, paid, credit, paidDriver, creditDriver,
      status, measureId, measureName) <> ((RequestRow.apply _).tupled, RequestRow.unapply)
  }


  private class ProductsTable(tag: Tag) extends Table[Product](tag, "product") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def cost = column[Double]("cost")
    def percent = column[Double]("percent")
    def price = column[Double]("price")
    def description = column[String]("description")
    def measureId = column[Long]("measureId")
    def measureName = column[String]("measureName")
    def currentAmount = column[Int]("currentAmount")
    def stockLimit = column[Int]("stockLimit")
    def type_1 = column[String]("type")
    def * = (id, name, cost, percent, price, description, measureId, measureName, currentAmount, stockLimit, type_1) <> ((Product.apply _).tupled, Product.unapply)
  }

  private val tableProduct = TableQuery[ProductsTable]

  private val tableQ = TableQuery[RequestRowTable]

  def create(
    requestId: Long, productId: Long, productName: String, quantity: Int, price: Double,
    totalPrice: Double, paid: Double, credit: Double, paidDriver: Double, creditDriver: Double,
    status: String, measureId: Long, measureName: String, userId: Long,
    userName: String): Future[RequestRow] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.REQUEST_ROW, userId, userName, productName + "( " + quantity + ")");
    (tableQ.map(p => (
      p.requestId, p.productId, p.productName, p.quantity, p.price, p.totalPrice,
      p.paid, p.credit, p.paidDriver, p.creditDriver, p.status, p.measureId,
      p.measureName))
      returning tableQ.map(_.id)
      into ((nameAge, id) => RequestRow(
        id, nameAge._1, nameAge._2, nameAge._3, nameAge._4,
        nameAge._5, nameAge._6, nameAge._7, nameAge._8,
        nameAge._9, nameAge._10, nameAge._11, nameAge._12, nameAge._13))) += (
        requestId, productId, productName, quantity, price, totalPrice,
        paid, credit, paidDriver, creditDriver, status, measureId, measureName)
  }

  def list(): Future[Seq[RequestRow]] = db.run {
    tableQ.result
  }

  def listWithProduct(): Future[Seq[(Long, String, Int)]] = db.run {
    val implicitInnerJoin = for {
      c <- tableQ
      s <- tableProduct if c.productId === s.id
    } yield (c.id, s.name, s.currentAmount)
    implicitInnerJoin.result
  }

  def listByParent(id: Long): Future[Seq[RequestRow]] = db.run {
    tableQ.filter(_.requestId === id).result
  }

  def listByQuantity(): Future[Seq[RequestRow]] = db.run {
    tableQ.filter(_.quantity > 0).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[RequestRow]] = db.run {
    tableQ.filter(_.id === id).result
  }

  def getListNames(): Future[Seq[(Long, Long)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.id)).result
  }

  // update required to copy
  def update(id: Long, requestId: Long, productId: Long, productName: String,
    quantity: Int, price: Double, totalPrice: Double,
    status: String, measureId: Long, measureName: String,
    userId: Long, userName: String): Future[Seq[RequestRow]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.REQUEST_ROW, userId, userName, productName + "( " + quantity + ")");

    db.run(tableQ.filter(_.id === id).map(s => (
      requestId, productId, productName, quantity,
      price, totalPrice, status, measureId,
      measureName)).update(requestId, productId, productName, quantity,
      price, totalPrice, status, measureId,
      measureName))

    /*val q2 = for { c <- tableQ if c.id === id } yield c.requestId
    db.run(q2.update(requestId))
    val q3 = for { c <- tableQ if c.id === id } yield c.productId
    db.run(q3.update(productId))
    val q31 = for { c <- tableQ if c.id === id } yield c.productName
    db.run(q31.update(productName))
    val q4 = for { c <- tableQ if c.id === id } yield c.quantity
    db.run(q4.update(quantity))

    val q5 = for { c <- tableQ if c.id === id } yield c.price
    db.run(q5.update(price))

    val qtotalPrice = for { c <- tableQ if c.id === id } yield c.totalPrice
    db.run(qtotalPrice.update(totalPrice))

    val qtotalPrice = for { c <- tableQ if c.id === id } yield c.totalPrice
    db.run(qtotalPrice.update(totalPrice))

    val qtotalPrice = for { c <- tableQ if c.id === id } yield c.totalPrice
    db.run(qtotalPrice.update(totalPrice))

    val q6 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q6.update(status))
    val q7 = for { c <- tableQ if c.id === id } yield c.measureId
    db.run(q7.update(measureId))
    val q8 = for { c <- tableQ if c.id === id } yield c.measureName
    db.run(q8.update(measureName))*/
    tableQ.filter(_.id === id).result
  }

  // Update the status to finalizado status
  def fillById(id: Long, productId: Long, quantity: Int): Future[Seq[RequestRow]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("entregado"))
    // Update the inventory
    repoInsum.updateInventary(productId, -quantity)
    tableQ.filter(_.id === id).result
  }

  def getByParentId(id: Long): Future[Seq[RequestRow]] = db.run {
    tableQ.filter(_.requestId === id).result
  }

  // Update the status to enviado status
  def updatePaid(id: Long, monto: Int): Future[Seq[RequestRow]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.paid
    getById(id).map { row =>
      db.run(q.update(row(0).paid + monto))
    }
    tableQ.filter(_.id === id).result
  }

  def updateRequestRow(id: Long, paid: Double, credit: Double) = {
    var row: RequestRow = new RequestRow(0, 0, 0, "productName", 1,
      0, 0, 0, 0, 0,
      0, "status", 0, "measureName")
    Await.result(getById(id).map { rows =>
      row = rows(0)
    }, 100.millis)

    val q = for { c <- tableQ if c.id === id } yield (c.paid, c.credit)
    db.run(q.update(row.paid + paid, row.credit + credit))
  }

  def updateRequestRowDriver(id: Long, paid: Double, credit: Double) = {
    var row: RequestRow = new RequestRow(0, 0, 0, "productName", 1,
      0, 0, 0, 0, 0,
      0, "status", 0, "measureName")
    Await.result(getById(id).map { rows =>
      row = rows(0)
    }, 100.millis)

    val q = for { c <- tableQ if c.id === id } yield (c.paidDriver, c.creditDriver)
    db.run(q.update(row.paidDriver + paid, row.creditDriver + credit))
  }

  // delete required
  def delete(id: Long): Future[Seq[RequestRow]] = db.run {
    getById(id).map { row =>
      if (row(0).status == "entregado") {
        repoInsum.updateInventary(row(0).productId, row(0).quantity)
      }
      val q = tableQ.filter(_.id === id)
      val action = q.delete
      val affectedRowsCount: Future[Int] = db.run(action)
    }
    tableQ.result
  }

}
