package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Product

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,
  repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

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
    def type_1 = column[String]("type")
    def * = (id, name, cost, percent, price, description, measureId, measureName, currentAmount, type_1) <> ((Product.apply _).tupled, Product.unapply)
  }

  private val tableQ = TableQuery[ProductsTable]

  def create(
    name: String, cost: Double, percent: Double, price: Double,
    description: String, measureId: Long, measureName: String,
    currentAmount: Int, type_1: String, userId: Long, userName: String): Future[Product] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.PRODUCT, userId, userName, name);
    (tableQ.map(
      p => (
        p.name, p.cost, p.percent, p.price, p.description,
        p.measureId, p.measureName, p.currentAmount, p.type_1)) returning tableQ.map(_.id) into (
        (nameAge, id) => Product(
          id, nameAge._1, nameAge._2, nameAge._3,
          nameAge._4, nameAge._5, nameAge._6,
          nameAge._7, nameAge._8, nameAge._9))) += (name, cost, percent, cost + cost * percent, description, measureId, measureName, currentAmount, type_1)
  }

  def list(): Future[Seq[Product]] = db.run {
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(300).map(s => (s.id, s.name)).result
  }

  def getListNamesById(id: Long): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id === id).map(s => (s.id, s.name)).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Product]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, name: String, cost: Double, percent: Double, price: Double,
    description: String, measureId: Long, measureName: String,
    currentAmount: Int, type_1: String, userId: Long, userName: String): Future[Seq[Product]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.PRODUCT, userId, userName, name)

    val q = for { c <- tableQ if c.id === id } yield c.name
    db.run(q.update(name))
    val q2 = for { c <- tableQ if c.id === id } yield c.percent
    db.run(q2.update(percent))
    val q3 = for { c <- tableQ if c.id === id } yield c.cost
    db.run(q3.update(cost))
    val q31 = for { c <- tableQ if c.id === id } yield c.price
    db.run(q31.update(cost + cost * percent))
    val q4 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q4.update(description))
    val q5 = for { c <- tableQ if c.id === id } yield c.measureId
    db.run(q5.update(measureId))
    val q51 = for { c <- tableQ if c.id === id } yield c.measureName
    db.run(q51.update(measureName))
    val q6 = for { c <- tableQ if c.id === id } yield c.currentAmount
    db.run(q6.update(currentAmount))
    val q7 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q7.update(type_1))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Product]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    tableQ.result
  }

  def updateAmount(insumoId: Long, amount: Int) = {
    val q = for { c <- tableQ if c.id === insumoId } yield c.currentAmount
    db.run(tableQ.filter(_.id === insumoId).result).map(s => s.map(insumoObj =>
      db.run(q.update(amount + insumoObj.currentAmount))))
  }

  def updateInventary(insumoId: Long, amount: Int) = {
    val q = for { c <- tableQ if c.id === insumoId } yield c.currentAmount
    db.run(tableQ.filter(_.id === insumoId).result).map(s => s.map(insumoObj =>
      db.run(q.update(amount + insumoObj.currentAmount))))
  }

  def getTotal(): Future[Int] = db.run {
    tableQ.length.result
  }

  def searchProduct(search: String): Future[Seq[Product]] = db.run {
    if (!search.isEmpty) {
      tableQ.filter(p => (p.name like "%" + search + "%")).drop(0).take(100).result
    } else {
      tableQ.drop(0).take(100).result
    }
  }
}
