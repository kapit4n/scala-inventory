package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Customer
import models.Company

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class CustomerRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class CustomeresTable(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def carnet = column[Int]("carnet")
    def telefono = column[Int]("telefono")
    def direccion = column[String]("direccion")
    def account = column[String]("account")
    def module = column[Long]("module")
    def moduleName = column[String]("moduleName")
    def companyName = column[String]("companyName")
    def totalDebt = column[Double]("totalDebt")
    def numberPayment = column[Int]("numberPayment")
    def position = column[String]("position")
    def * = (
      id, name, carnet, telefono, direccion, account, module, moduleName,
      companyName, totalDebt, numberPayment, position) <> ((Customer.apply _).tupled, Customer.unapply)
  }

  private class CompanysTable(tag: Tag) extends Table[Company](tag, "company") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (
      id, name) <> ((Company.apply _).tupled, Company.unapply)
  }

  private val tableQ = TableQuery[CustomeresTable]
  private val tableQCompany = TableQuery[CompanysTable]

  def create(name: String, carnet: Int, telefono: Int, direccion: String,
    account: String, module: Long, moduleName: String,
    userId: Long, userName: String): Future[Customer] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.PRODUCTOR, userId, userName, name);
    (tableQ.map(
      p => (
        p.name, p.carnet, p.telefono, p.direccion, p.account,
        p.module, p.moduleName, p.companyName, p.totalDebt, p.numberPayment,
        p.position)) returning tableQ.map(_.id) into (
        (nameAge, id) =>
          Customer(
            id, nameAge._1, nameAge._2,
            nameAge._3, nameAge._4, nameAge._5,
            nameAge._6, nameAge._7, nameAge._8,
            nameAge._9, nameAge._10, nameAge._11))) += (
          name, carnet, telefono, direccion, account,
          module, moduleName, "", 0, 0, "Customer")
  }

  def list(start: Int, interval: Int): Future[Seq[Customer]] = db.run {
    tableQ.drop(start).take(interval).result
  }

  def listByCompany(id: Long): Future[Seq[Customer]] = db.run {
    tableQ.take(500).result
  }

  def listCompany(): Future[Seq[Company]] = db.run {
    tableQCompany.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Customer]] = db.run {
    tableQ.filter(_.id === id).result
  }

  def getCompanyById(id: Long): Future[Seq[Company]] = db.run {
    tableQCompany.filter(_.id === id).result
  }

  // update required to copy
  def update(
    id: Long, name: String, carnet: Int, telefono: Int,
    direccion: String, account: String, module: Long,
    moduleName: String, companyName: String,
    totalDebt: Double, numberPayment: Int,
    userId: Long, userName: String): Future[Seq[Customer]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.PRODUCTOR, userId, userName, name);
    val q = for { c <- tableQ if c.id === id } yield c.name
    db.run(q.update(name))
    val q2 = for { c <- tableQ if c.id === id } yield c.carnet
    db.run(q2.update(carnet))
    val q3 = for { c <- tableQ if c.id === id } yield c.telefono
    db.run(q3.update(telefono))
    val q4 = for { c <- tableQ if c.id === id } yield c.account
    db.run(q4.update(account))
    val q5 = for { c <- tableQ if c.id === id } yield c.module
    db.run(q5.update(module))
    val q6 = for { c <- tableQ if c.id === id } yield c.direccion
    db.run(q6.update(direccion))
    val q7 = for { c <- tableQ if c.id === id } yield c.moduleName
    db.run(q7.update(moduleName))
    val q8 = for { c <- tableQ if c.id === id } yield c.totalDebt
    db.run(q8.update(totalDebt))
    val q9 = for { c <- tableQ if c.id === id } yield c.numberPayment
    db.run(q9.update(numberPayment))

    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Customer]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.name)).result
  }

  def getListNamesCompanys(): Future[Seq[Company]] = db.run {
    tableQCompany.result
  }

  def list100Customers(): Future[Seq[Customer]] = db.run {
    tableQ.take(100).result
  }

  def list100CustomersDebt(): Future[Seq[Customer]] = db.run {
    tableQ.filter(_.totalDebt > 0.0).take(100).result
  }

  def searchByAccount(acc: String): Future[Seq[Customer]] = db.run {
    tableQ.filter(_.account like "%" + acc + "%").take(100).result
  }

  def searchByName(name: String): Future[Seq[Customer]] = db.run {
    tableQ.filter(_.name.toLowerCase like "%" + name.toLowerCase + "%").take(100).result
  }

  def getTotal(): Future[Int] = db.run {
    tableQ.length.result
  }

  // Update the status to enviado status
  def updateTotalDebt(id: Long, monto: Double): Future[Seq[Customer]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.totalDebt
    getById(id).map { row =>
      db.run(q.update(row(0).totalDebt + monto))
    }
    tableQ.filter(_.id === id).result
  }

  // Update it when generate the report
  def updateNumberPayment(id: Long, numberPayment: Int): Future[Seq[Customer]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.numberPayment
    getById(id).map { row =>
      db.run(q.update(row(0).numberPayment + numberPayment))
    }
    tableQ.filter(_.id === id).result
  }

  def listByTotalDebt(): Future[Seq[Customer]] = db.run {
    tableQ.filter(_.totalDebt > 0.0).result
  }

  def getByAccount2(account: String): Future[Seq[Customer]] = db.run {
    tableQ.filter(_.account like "%" + account + "%").result
  }

  def searchCustomer(search: String): Future[Seq[Customer]] = db.run {
    if (!search.isEmpty) {
      tableQ.filter(p => (p.account like "%" + search + "%") || (p.name like "%" + search + "%")
        || (p.companyName like "%" + search + "%")).drop(0).take(100).result
    } else {
      tableQ.drop(0).take(100).result
    }
  }

  def searchCustomerDebs(search: String): Future[Seq[Customer]] = db.run {
    if (!search.isEmpty) {
      tableQ.filter(p => (p.account like "%" + search + "%") || (p.name like "%" + search + "%")
        || (p.companyName like "%" + search + "%") && p.totalDebt > 0.0).drop(0).take(100).result
    } else {
      tableQ.drop(0).take(100).result
    }
  }

}
