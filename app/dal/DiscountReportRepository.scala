package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.DiscountReport

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class DiscountReportRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class DiscountReportTable(tag: Tag) extends Table[DiscountReport](tag, "discountReport") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def startDate = column[String]("startDate")
    def endDate = column[String]("endDate")
    def status = column[String]("status")
    def total = column[Double]("total")
    def * = (id, startDate, endDate, status, total) <> ((DiscountReport.apply _).tupled, DiscountReport.unapply)
  }

  private val tableQ = TableQuery[DiscountReportTable]

  def create(startDate: String, endDate: String, status: String): Future[DiscountReport] = db.run {
    (tableQ.map(p => (p.startDate, p.endDate, p.status, p.total))
      returning tableQ.map(_.id)
      into ((nameAge, id) => DiscountReport(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))) += (startDate, endDate, status, 0.0)
  }

  def list(): Future[Seq[DiscountReport]] = db.run {
    tableQ.result
  }

  def listByEmployee(id: Long): Future[Seq[DiscountReport]] = db.run {
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.startDate)).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[DiscountReport]] = db.run {
    tableQ.filter(_.id === id).result
  }

  def generarReport(datos: Map[Long, String]) = {
    println("Generating data")
    println(datos)
  }

  // update required to copy
  def update(id: Long, startDate: String, endDate: String, status: String, total: Double): Future[Seq[DiscountReport]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.startDate
    db.run(q.update(startDate))
    val q2 = for { c <- tableQ if c.id === id } yield c.endDate
    db.run(q2.update(endDate))
    val q3 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q3.update(status))
    val q4 = for { c <- tableQ if c.id === id } yield c.total
    db.run(q4.update(total))
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def sendById(id: Long): Future[Seq[DiscountReport]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("enviado"))
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def finalizeById(id: Long): Future[Seq[DiscountReport]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("finalizado"))
    tableQ.filter(_.id === id).result
  }

  // Update the status to finalizado status
  def finishById(id: Long): Future[Seq[DiscountReport]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("finalizado"))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[DiscountReport]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    tableQ.result
  }

  // Update the status to enviado status
  def updateTotal(id: Long, monto: Double): Future[Seq[DiscountReport]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.total
    getById(id).map { row =>
      db.run(q.update(monto))
    }
    tableQ.filter(_.id === id).result
  }
  // Update the status to enviado status
  def addToTotal(id: Long, monto: Double): Future[Seq[DiscountReport]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.total
    getById(id).map { rows =>
      db.run(q.update(monto + rows(0).total))
    }
    tableQ.filter(_.id === id).result
  }
}
