package dal

import scala.concurrent.duration._
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.LogEntry
import models.LogEntryShow

import scala.concurrent.{ Future, ExecutionContext, Await }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class LogEntryRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val ACCOUNT = "Account"
  val ASSOCIATION = "Company"
  val COMPANY = "Configuracion"
  val DISCOUNT_DETAIL = "Detail of Discount"
  val DISCOUNT_REPORT = "Report of Discount"
  val MEASURE = "Unit of Measure"
  val MODULE = "Modulo"
  val PRODUCT = "Product"
  val PRODUCT_INV = "Income de Product"
  val PRODUCT_REQUEST = "Order"
  val PRODUCT_REQUEST_BY_INSUMO = "Order"
  val PRODUCTOR = "Customer"
  val VENDOR = "Vendor"
  val VENDOR_CONTRACT = "Vendor Contract"
  val REPORTE = "Report"
  val REQUEST_ROW = "Detail of Report"
  val REQUEST_ROW_PRODUCTOR = "Entrega de Product al customer"
  val ROLE = "Rol"
  val TRANSACTION = "Transaction"
  val TRANSACTION_DETAIL = "Detail of transacion"
  val USER = "User"
  val USER_PERMISSION = "Permiso"
  val USER_ROLE = "Assignacion de Permiso"

  val CREATE = "Created"
  val UPDATE = "Actualizado"
  val LOGIN = "Login"

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class LogEntrysTable(tag: Tag) extends Table[LogEntry](tag, "logEntry") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def action = column[String]("action")
    def tableName1 = column[String]("tableName1")
    def userId = column[Long]("userId")
    def userName = column[String]("userName")
    def description = column[String]("description")
    def * = (id, action, tableName1, userId, userName, description) <> ((LogEntry.apply _).tupled, LogEntry.unapply)
  }

  private class LogEntrysShowTable(tag: Tag) extends Table[LogEntryShow](tag, "logEntry") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def action = column[String]("action")
    def tableName1 = column[String]("tableName1")
    def userId = column[Long]("userId")
    def userName = column[String]("userName")
    def description = column[String]("description")
    def createdAt = column[String]("createdAt")
    def * = (id, action, tableName1, userId, userName, description, createdAt) <> ((LogEntryShow.apply _).tupled, LogEntryShow.unapply)
  }

  private val tableQ = TableQuery[LogEntrysTable]
  private val tableQShow = TableQuery[LogEntrysShowTable]

  def create(action: String, tableName1: String, userId: Long, userName: String, description: String): Future[LogEntry] = db.run {
    val description_1 = tableName1 + " (" + description + ") fue " + action + " por " + userName
    (tableQ.map(p => (p.action, p.tableName1, p.userId, p.userName, p.description))
      returning tableQ.map(_.id)
      into ((nameAge, id) => LogEntry(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))) += (action, tableName1, userId, userName, description_1)
  }

  def createLogEntry(action: String, tableName1: String, userId: Long, userName: String, description: String) = {
    Await.result(create(action, tableName1, userId, userName, description).map(res => print("DONE")), 3000.millis)
  }

  def list(): Future[Seq[LogEntryShow]] = db.run {
    tableQShow.result
  }

  def getById(id: Long): Future[Seq[LogEntryShow]] = db.run {
    tableQShow.filter(_.id === id).result
  }
}
