package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Vendor

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class VendorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class VendorTable(tag: Tag) extends Table[Vendor](tag, "vendor") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def phone = column[Int]("phone")
    def address = column[String]("address")
    def contact = column[String]("contact")
    def account = column[Long]("account")
    def * = (id, name, phone, address, contact, account) <> ((Vendor.apply _).tupled, Vendor.unapply)
  }

  private val tableQ = TableQuery[VendorTable]

  def create(
    name: String, phone: Int, address: String, contact: String,
    account: Long, userId: Long, userName: String): Future[Vendor] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.VENDOR_CONTRACT, userId, userName, name);
    (tableQ.map(p => (p.name, p.phone, p.address, p.contact, p.account))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Vendor(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))) += (name, phone, address, contact, account)
  }

  def list(): Future[Seq[Vendor]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Vendor]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, name: String, phone: Int, address: String, contact: String,
    account: Long, userId: Long, userName: String): Future[Seq[Vendor]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.VENDOR_CONTRACT, userId, userName, name);
    val q = for { c <- tableQ if c.id === id } yield c.name
    db.run(q.update(name))
    val q3 = for { c <- tableQ if c.id === id } yield c.phone
    db.run(q3.update(phone))
    val q2 = for { c <- tableQ if c.id === id } yield c.address
    db.run(q2.update(address))
    val q4 = for { c <- tableQ if c.id === id } yield c.contact
    db.run(q4.update(contact))
    val q5 = for { c <- tableQ if c.id === id } yield c.account
    db.run(q5.update(account))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Vendor]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    tableQ.result
  }

  // get list of names
  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.name)).result
  }

  // get list of names
  def getListByIds(vendors: Seq[Long]): Future[Seq[Vendor]] = db.run {
    tableQ.filter(_.id inSetBind vendors).result
  }

}
