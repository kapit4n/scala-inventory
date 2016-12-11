package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.TransactionDetail
import models.Transaction

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class TransactionDetailRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class TransactionDetailsTable(tag: Tag) extends Table[TransactionDetail](tag, "transactionDetail") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def transactionId = column[Long]("transaction")
    def accountId = column[Long]("account")
    def debit = column[Double]("debit")
    def credit = column[Double]("credit")
    def transactionDate = column[String]("transactionDate")
    def accountCode = column[String]("accountCode")
    def accountName = column[String]("accountName")
    def * = (id, transactionId, accountId, debit, credit, transactionDate, accountCode, accountName) <> ((TransactionDetail.apply _).tupled, TransactionDetail.unapply)
  }

  private val tableQ = TableQuery[TransactionDetailsTable]

  def create(transactionId: Long, accountId: Long, debit: Double, credit: Double, userId: Long, userName: String): Future[TransactionDetail] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.TRANSACTION_DETAIL, userId, userName, accountId.toString);
    (tableQ.map(p => (p.transactionId, p.accountId, p.debit, p.credit, p.transactionDate, p.accountCode, p.accountName))
      returning tableQ.map(_.id)
      into ((nameAge, id) => TransactionDetail(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6, nameAge._7))) += (transactionId, accountId, debit, credit, "", "", "")
  }

  def list(): Future[Seq[TransactionDetail]] = db.run {
    tableQ.result
  }

  def listByTransaction(id: Long): Future[Seq[TransactionDetail]] = db.run {
    tableQ.filter(_.transactionId === id).result
  }

  def listByAccount(id: Long): Future[Seq[TransactionDetail]] = db.run {
    tableQ.filter(_.accountId === id).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[TransactionDetail]] = db.run {
    tableQ.filter(_.id === id).result
  }

  def setTransactionDetailsList(trans: Transaction) = {
    println("Going to get the values")
    listByTransaction(trans.id).map { res => trans.details = res; println("After res"); ; println(trans); println(trans.details); }
  }

  def updateTransactionParams(id: Long, transactionDate: String) = {
    val q = for { c <- tableQ if c.id === id } yield c.transactionDate
    db.run(q.update(transactionDate))
  }

  def updateAccountParams(id: Long, accountCode: String, accountName: String) = {
    val q = for { c <- tableQ if c.id === id } yield c.accountCode
    db.run(q.update(accountCode))
    val q1 = for { c <- tableQ if c.id === id } yield c.accountName
    db.run(q1.update(accountName))
  }

  // update required to copy
  def update(id: Long, transactionId: Long, accountId: Long, debit: Double, credit: Double, userId: Long, userName: String): Future[Seq[TransactionDetail]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.TRANSACTION_DETAIL, userId, userName, accountId.toString);
    val q = for { c <- tableQ if c.id === id } yield c.transactionId
    db.run(q.update(transactionId))
    val q2 = for { c <- tableQ if c.id === id } yield c.accountId
    db.run(q2.update(accountId))
    val q3 = for { c <- tableQ if c.id === id } yield c.debit
    db.run(q3.update(debit))
    val q4 = for { c <- tableQ if c.id === id } yield c.credit
    db.run(q4.update(credit))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[TransactionDetail]] = db.run {
    val q = tableQ.filter(_.id === id)
    val res = q.result
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action);
    affectedRowsCount.map(s => println(s))
    res
  }
}
