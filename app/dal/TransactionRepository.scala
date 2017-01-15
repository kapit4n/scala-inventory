package dal

import scala.concurrent.duration._
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Transaction
import models.TransactionDetail

import scala.concurrent.{ Future, ExecutionContext, Await }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class TransactionRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
  repoTransDetail: TransactionDetailRepository,
  repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class TransactionTable(tag: Tag) extends Table[Transaction](tag, "transaction") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def date = column[String]("date")
    def type_1 = column[String]("type")
    def description = column[String]("description")
    def createdBy = column[Long]("createdBy")
    def createdByName = column[String]("createdByName")
    def receivedBy = column[Long]("receivedBy")
    def receivedByName = column[String]("receivedByName")
    def autorizedBy = column[Long]("autorizedBy")
    def autorizedByName = column[String]("autorizedByName")
    def * = (
      id, date, type_1, description, createdBy,
      createdByName, receivedBy,
      receivedByName, autorizedBy, autorizedByName) <> ((Transaction.apply _).tupled, Transaction.unapply)
  }

  private val tableQ = TableQuery[TransactionTable]

  def createIncome(
    date: String, type_1: String, description: String,
    createdBy: Long, createdByName: String,
    userId: Long, userName: String): Future[Transaction] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.TRANSACTION, userId, userName, date + " " + type_1 + " " + description);
    (tableQ.map(p => (
      p.date, p.type_1, p.description,
      p.createdBy, p.createdByName,
      p.receivedBy, p.receivedByName,
      p.autorizedBy, p.autorizedByName)) returning tableQ.map(_.id)
      into ((nameAge, id) => Transaction(
        id, nameAge._1, nameAge._2, nameAge._3,
        nameAge._4, nameAge._5, nameAge._6, nameAge._7, nameAge._8, nameAge._9))) += (
        date, type_1, description,
        createdBy, createdByName, 0, "", 0, "")
  }
  def create(
    date: String, type_1: String, description: String, createdBy: Long,
    createdByName: String, receivedBy: Long,
    receivedByName: String, autorizedBy: Long, autorizedByName: String,
    userId: Long, userName: String): Future[Transaction] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.TRANSACTION, userId, userName, date + " " + type_1 + " " + description);
    (tableQ.map(p => (
      p.date, p.type_1, p.description,
      p.createdBy, p.createdByName,
      p.receivedBy, p.receivedByName,
      p.autorizedBy, p.autorizedByName)) returning tableQ.map(_.id)
      into ((nameAge, id) => Transaction(
        id, nameAge._1, nameAge._2, nameAge._3,
        nameAge._4, nameAge._5, nameAge._6, nameAge._7, nameAge._8, nameAge._9))) += (
        date, type_1, description,
        createdBy, createdByName,
        receivedBy, receivedByName,
        autorizedBy, autorizedByName)
  }

  def list(): Future[Seq[Transaction]] = db.run {
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.date)).result
  }

  def getListNamesById(id: Long): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id === id).map(s => (s.id, s.date)).result
  }

  def getListPopulated(): Future[Seq[Transaction]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Transaction]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def updateIncome(
    id: Long, date: String, type_1: String, description: String,
    userId: Long, userName: String): Future[Seq[Transaction]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.TRANSACTION, userId, userName, date + " " + type_1 + " " + description);
    val q = for { c <- tableQ if c.id === id } yield c.date
    db.run(q.update(date))
    val q4 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q4.update(type_1))
    val q5 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q5.update(description))

    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(
    id: Long, date: String, type_1: String, description: String,
    receivedBy: Long, receivedByName: String,
    autorizedBy: Long, autorizedByName: String,
    userId: Long, userName: String): Future[Seq[Transaction]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.TRANSACTION, userId, userName, date + " " + type_1 + " " + description);
    val q = for { c <- tableQ if c.id === id } yield c.date
    db.run(q.update(date))
    val q4 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q4.update(type_1))
    val q5 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q5.update(description))

    val q6 = for { c <- tableQ if c.id === id } yield c.receivedBy
    db.run(q6.update(receivedBy))
    val q71 = for { c <- tableQ if c.id === id } yield c.receivedByName
    db.run(q71.update(receivedByName))

    val q8 = for { c <- tableQ if c.id === id } yield c.autorizedBy
    db.run(q8.update(autorizedBy))
    val q9 = for { c <- tableQ if c.id === id } yield c.autorizedByName
    db.run(q9.update(autorizedByName))

    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Transaction]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    tableQ.result
  }
}
