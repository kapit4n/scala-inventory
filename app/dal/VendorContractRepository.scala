package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.VendorContract
import java.sql.Date

import scala.concurrent.{ Future, ExecutionContext }


/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class VendorContractRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,
  repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class VendorContractTable(tag: Tag) extends Table[VendorContract](tag, "vendorContract") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def vendorId = column[Long]("vendorId")
    def startDate = column[String]("startDate")
    def endDate = column[String]("endDate")
    def * = (id, vendorId, startDate, endDate) <> ((VendorContract.apply _).tupled, VendorContract.unapply)
  }

  private val tableQ = TableQuery[VendorContractTable]

  def create(vendorId: Long, startDate: String, endDate: String, userId: Long,
    userName: String): Future[VendorContract] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.VENDOR, userId, userName, vendorId.toString);
    (tableQ.map(p => (p.vendorId, p.startDate, p.endDate))
      returning tableQ.map(_.id)
      into ((nameAge, id) => VendorContract(id, nameAge._1, nameAge._2, nameAge._3))) += (vendorId, startDate, endDate)
  }

  def list(): Future[Seq[VendorContract]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[VendorContract]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, vendorId: Long, startDate: String, endDate: String, userId: Long, userName: String): Future[Seq[VendorContract]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.VENDOR, userId, userName, vendorId.toString);
    val q = for { c <- tableQ if c.id === id } yield c.vendorId
    db.run(q.update(vendorId))
    val q3 = for { c <- tableQ if c.id === id } yield c.startDate
    db.run(q3.update(startDate))
    val q2 = for { c <- tableQ if c.id === id } yield c.endDate
    db.run(q2.update(endDate))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[VendorContract]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    tableQ.result
  }

  // get list of vendorIds
  def getListNames(): Future[Seq[(Long, Long)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.vendorId)).result
  }

  // get list of vendorIds
  def getListByIds(vendors: Seq[Long]): Future[Seq[VendorContract]] = db.run {
    tableQ.filter(_.id inSetBind vendors).result
  }

}
