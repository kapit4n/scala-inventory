package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Measure

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class MeasureRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, repoLog: LogEntryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class MeasuresTable(tag: Tag) extends Table[Measure](tag, "measure") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def quantity = column[Double]("quantity")
    def description = column[String]("description")
    def measureId = column[Long]("measureId")
    def measureName = column[String]("measureName")
    def * = (id, name, quantity, description, measureId, measureName) <> ((Measure.apply _).tupled, Measure.unapply)
  }

  private val tableQ = TableQuery[MeasuresTable]

  def create(name: String, quantity: Double, description: String, measureId: Long, measureName: String, userId: Long, userName: String): Future[Measure] = db.run {
    repoLog.createLogEntry(repoLog.CREATE, repoLog.MEASURE, userId, userName, name);
    (tableQ.map(p => (p.name, p.quantity, p.description, p.measureId, p.measureName))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Measure(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))) += (name, quantity, description, measureId, measureName)
  }

  def list(): Future[Seq[Measure]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Measure]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, name: String, quantity: Double, description: String, measureId: Long, measureName: String, userId: Long, userName: String): Future[Seq[Measure]] = db.run {
    repoLog.createLogEntry(repoLog.UPDATE, repoLog.MEASURE, userId, userName, name);
    val q = for { c <- tableQ if c.id === id } yield c.name
    db.run(q.update(name))
    val q2 = for { c <- tableQ if c.id === id } yield c.quantity
    db.run(q2.update(quantity))
    val q3 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q3.update(description))
    val q4 = for { c <- tableQ if c.id === id } yield c.measureId
    db.run(q4.update(measureId))
    val q5 = for { c <- tableQ if c.id === id } yield c.measureName
    db.run(q5.update(measureName))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Measure]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.take(200).map(s => (s.id, s.name)).result
  }

}
