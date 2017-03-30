package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Account
import scala.concurrent.{ Future, ExecutionContext, Await }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class AccountRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class AccountesTable(tag: Tag) extends Table[Account](tag, "account") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def code = column[String]("code")
    def name = column[String]("name")
    def type_1 = column[String]("type")
    def negativo = column[String]("negativo")
    def parent = column[Long]("parent")
    def description = column[String]("description")
    def child = column[Boolean]("child")
    def debit = column[Double]("debit")
    def credit = column[Double]("credit")
    def * = (id, code, name, type_1, negativo, parent, description, child, debit, credit) <> ((Account.apply _).tupled, Account.unapply)
  }

  private val tableQ = TableQuery[AccountesTable]

  def create(code: String, name: String, type_1: String, negativo: String, parent: Long, description: String,
    userId: Long, userName: String): Future[Account] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.ACCOUNT, userId, userName, name)

    updateParentFlag(parent, false, 0)
    (tableQ.map(p => (p.code, p.name, p.type_1, p.negativo, p.parent, p.description, p.child, p.debit, p.credit))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Account(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6, nameAge._7, nameAge._8, nameAge._9))) += (code, name, type_1, negativo, parent, description, true, 0, 0)
  }

  def list(): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Account]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // to cpy
  def getByCode(code: String): Future[Seq[Account]] = db.run {
    tableQ.filter(_.code === code).result
  }

  def getByParent(id: Long): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).filter(_.parent === id).result
  }

  // to cpy
  def getByPasivo(): Future[Seq[Account]] = db.run {
    tableQ.filter(_.type_1 === "PASIVO").sortBy(m => (m.code)).result
  }

  // to cpy
  def getChilAccounts(): Future[Seq[Account]] = db.run {
    tableQ.filter(_.child === true).sortBy(m => (m.code)).result
  }

  def getChilAccountsByType(type_1: String): Future[Seq[Account]] = db.run {
    if (type_1 == "Income") {
      tableQ.filter(p => p.child === true && (
        p.type_1 === "INCOME" || p.type_1 === "PATRIMONIO" ||
        p.type_1 === "ACTIVO" || p.type_1 === "PASIVO")).sortBy(m => (m.code)).result
    } else {
      tableQ.filter(p => p.child === true && (
        p.type_1 === "OUTCOME" || p.type_1 === "PATRIMONIO" ||
        p.type_1 === "ACTIVO" || p.type_1 === "PASIVO")).sortBy(m => (m.code)).result
    }
  }

  // to cpy
  def getByActivo(): Future[Seq[Account]] = db.run {
    tableQ.filter(_.type_1 === "ACTIVO").sortBy(m => (m.code)).result
  }

  // to cpy
  def getByPatrimonio(): Future[Seq[Account]] = db.run {
    tableQ.filter(_.type_1 === "PATRIMONIO").sortBy(m => (m.code)).result
  }

  // update required to copy
  def update(id: Long, code: String, name: String, type_1: String, negativo: String, parent: Long, description: String, userId: Long, userName: String): Future[Seq[Account]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.ACCOUNT, userId, userName, name);
    val q = for { c <- tableQ if c.id === id } yield c.code
    db.run(q.update(code))
    val q2 = for { c <- tableQ if c.id === id } yield c.name
    db.run(q2.update(name))
    val q3 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q3.update(type_1))
    val q4 = for { c <- tableQ if c.id === id } yield c.negativo
    db.run(q4.update(negativo))
    val q5 = for { c <- tableQ if c.id === id } yield c.parent
    getById(id).map { res =>
      if (res(0).parent != parent) {
        updateParentFlag(parent, false, id)
        updateParentFlag(res(0).parent, true, id)
        db.run(q5.update(parent))
      }
    }
    val q6 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q6.update(description))
    tableQ.filter(_.id === id).result
  }

  // Update the parent flag to true or false
  def updateParentFlag(id: Long, flag: Boolean, actualId: Long): Future[Seq[Account]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.child
    if (!flag) {
      db.run(q.update(flag))
    } else {
      getByParent(id).map { res =>

        if (res.length == 0) {
          db.run(q.update(flag))
        } else if (res.length == 1 && res(0).id == actualId) {
          db.run(q.update(flag))
        }
      }
    }
    tableQ.filter(_.id === id).result
  }

  def updateParentDebitCredit(id: Long, debit: Double, credit: Double): Future[Seq[Account]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.debit
    val q2 = for { c <- tableQ if c.id === id } yield c.credit
    getById(id).map { res =>
      db.run(q.update(debit + res(0).debit))
      db.run(q2.update(credit + res(0).credit))
      if (res(0).parent > 0) {
        updateParentDebitCredit(res(0).parent, debit, credit)
      }
    }
    tableQ.filter(_.id === id).result
  }

  def delete(id: Long): Future[Seq[Account]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.sortBy(m => (m.code)).map(s => (s.id, s.name)).result
  }

  def getListObjs(): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).sortBy(m => (m.code)).result
  }

  def getListObjsChild(): Future[Seq[Account]] = db.run {
    tableQ.filter(_.child === true).sortBy(m => (m.code)).sortBy(m => (m.code)).result
  }

  def searchAccount(search: String): Future[Seq[Account]] = db.run {
    if (!search.isEmpty) {
      tableQ.filter(p => (p.name like "%" + search + "%") || (p.type_1 like "%" + search + "%") || (p.code like "%" + search + "%")).drop(0).take(100).result
    } else {
      tableQ.drop(0).take(100).result
    }
  }
}
