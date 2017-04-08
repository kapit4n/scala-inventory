package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.ProductRequest
import models.Product

import scala.concurrent.{ Future, ExecutionContext, Await }
import scala.concurrent.duration._

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductRequestRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,
  repoRequestRow: RequestRowRepository,
  repoProduct: ProductRepository,
  repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProductRequestTable(tag: Tag) extends Table[ProductRequest](tag, "productRequest") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def date = column[String]("date")
    def employee = column[Long]("employee")
    def employeeName = column[String]("employeeName")
    def storekeeper = column[Long]("storekeeper")
    def storekeeperName = column[String]("storekeeperName")
    def status = column[String]("status")
    def detail = column[String]("detail")
    def type_1 = column[String]("type")
    def userId = column[Long]("user")
    def userName = column[String]("userName")

    def * = (
      id, date, employee, employeeName, storekeeper, storekeeperName,
      status, detail, type_1, userId, userName) <> ((ProductRequest.apply _).tupled, ProductRequest.unapply)
  }

  private val tableQ = TableQuery[ProductRequestTable]

  def create(
    date: String, employee: Long, employeeName: String, storekeeper: Long,
    storekeeperName: String, status: String, detail: String, type_1: String,
    userId: Long, userName: String): Future[ProductRequest] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.PRODUCT_REQUEST, userId, userName, date);
    (tableQ.map(p => (
      p.date, p.employee, p.employeeName, p.storekeeper,
      p.storekeeperName, p.status, p.detail, p.type_1, p.userId, p.userName))
      returning tableQ.map(_.id)
      into ((nameAge, id) => ProductRequest(
        id, nameAge._1, nameAge._2, nameAge._3, nameAge._4,
        nameAge._5, nameAge._6, nameAge._7, nameAge._8, nameAge._9, nameAge._10))) += (
        date, employee, employeeName,
        storekeeper, storekeeperName, status,
        detail, type_1, 0, "")
  }

  def createByInsumo(
    date: String, userId2: Long, userName2: String, status: String, detail: String, type_1: String,
    userId: Long, userName: String): Future[ProductRequest] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.PRODUCT_REQUEST, userId, userName, date);
    (tableQ.map(p => (
      p.date,
      p.employee, p.employeeName, p.storekeeper,
      p.storekeeperName, p.status, p.detail, p.type_1, p.userId, p.userName))
      returning tableQ.map(_.id)
      into ((nameAge, id) => ProductRequest(
        id, nameAge._1, nameAge._2, nameAge._3,
        nameAge._4, nameAge._5, nameAge._6, nameAge._7,
        nameAge._8, nameAge._9, nameAge._10))) += (date, 0, "", 0, "", status, detail, type_1, userId2, userName2)
  }

  def list(): Future[Seq[ProductRequest]] = db.run {
    tableQ.result
  }

  def listByEmployee(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.employee === id).result
  }

  def listByStorekeeper(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.storekeeper === id).result
  }

  def listByInsumoUser(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.employee === id).result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.date)).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(
    id: Long, date: String, employee: Long,
    employeeName: String, storekeeper: Long,
    storekeeperName: String, status: String,
    detail: String, type_1: String,
    userId: Long, userName: String): Future[Seq[ProductRequest]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.PRODUCT_REQUEST, userId, userName, date);
    val q = for { c <- tableQ if c.id === id } yield c.date
    db.run(q.update(date))
    val q2 = for { c <- tableQ if c.id === id } yield c.employee
    db.run(q2.update(employee))
    val q21 = for { c <- tableQ if c.id === id } yield c.employeeName
    db.run(q21.update(employeeName))
    val q3 = for { c <- tableQ if c.id === id } yield c.storekeeper
    db.run(q3.update(storekeeper))
    val q31 = for { c <- tableQ if c.id === id } yield c.storekeeperName
    db.run(q31.update(storekeeperName))
    val q4 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q4.update(status))
    val q5 = for { c <- tableQ if c.id === id } yield c.detail
    db.run(q5.update(detail))
    val q6 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q6.update(type_1))
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def updateByInsumo(
    id: Long, date: String, userId2: Long,
    userName2: String, status: String,
    detail: String, type_1: String,
    userId: Long, userName: String): Future[Seq[ProductRequest]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.PRODUCT_REQUEST, userId, userName, date);
    val q = for { c <- tableQ if c.id === id } yield c.date
    db.run(q.update(date))
    val q2 = for { c <- tableQ if c.id === id } yield c.userId
    db.run(q2.update(userId2))
    val q21 = for { c <- tableQ if c.id === id } yield c.userName
    db.run(q21.update(userName2))
    val q4 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q4.update(status))
    val q5 = for { c <- tableQ if c.id === id } yield c.detail
    db.run(q5.update(detail))
    val q6 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q6.update(type_1))
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def sendById(id: Long): Future[Seq[ProductRequest]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("enviado"))
    tableQ.filter(_.id === id).result
  }

  def runUpdateChildren(id: Long) = {
    Await.result(repoRequestRow.getByParentId(id).map { rowList =>
      rowList.foreach { row =>
        if (row.status == "enviado") {
          repoRequestRow.fillById(row.id, row.productId, row.quantity)
        }
      }
    }, 3000.millis)
  }

  // Update the status to finalizado status
  def finishById(id: Long): Future[Seq[ProductRequest]] = db.run {
    getById(id).map { pRequest =>
      if (pRequest(0).status == "enviado") {
        runUpdateChildren(id)
      }
      if (pRequest(0).status == "enviado") {
        val q = for { c <- tableQ if c.id === id } yield c.status
        db.run(q.update("finalizado"))
      }
    }
    tableQ.filter(_.id === id).result
  }

  def removeChildren(id: Long) = {
    Await.result(repoRequestRow.getByParentId(id).map { rowList =>
      rowList.foreach { row =>
        repoRequestRow.delete(row.id)
      }
    }, 3000.millis)
  }

  // delete required
  def delete(id: Long): Future[Seq[ProductRequest]] = db.run {

    removeChildren(id)
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    // remove all children here

    tableQ.result
  }
}
