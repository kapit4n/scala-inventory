package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Report

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ReportRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ReportsTable(tag: Tag) extends Table[Report](tag, "reportes") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def monto = column[Int]("monto")
    def account = column[Int]("account")
    def cliente = column[Int]("cliente")
    def * = (id, monto, account, cliente) <> ((Report.apply _).tupled, Report.unapply)
  }

  private val reportes = TableQuery[ReportsTable]

  def create(monto: Int, account: Int, cliente: Int): Future[Report] = db.run {
    (reportes.map(p => (p.monto, p.account, p.cliente))
      returning reportes.map(_.id)
      into ((nameAge, id) => Report(id, nameAge._1, nameAge._2, nameAge._3))) += (monto, account, cliente)
  }

  def list(): Future[Seq[Report]] = db.run {
    reportes.result
  }
}
