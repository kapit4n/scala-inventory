package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Setting

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class SettingRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class SettingsTable(tag: Tag) extends Table[Setting](tag, "setting") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def president = column[String]("president")
    def language = column[String]("language")
    def description = column[String]("description")
    def * = (id, name, president, language, description) <> ((Setting.apply _).tupled, Setting.unapply)
  }

  private val tableQ = TableQuery[SettingsTable]

  def create(name: String, president: String, language: String, description: String): Future[Setting] = db.run {
    (tableQ.map(p => (p.name, p.president, p.language, p.description))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Setting(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))) += (name, president, language, description)
  }

  // to cpy
  def getFirst(): Future[Seq[Setting]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Setting]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, name: String, president: String, language: String, description: String): Future[Seq[Setting]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.name
    db.run(q.update(name))
    val q2 = for { c <- tableQ if c.id === id } yield c.president
    db.run(q2.update(president))
    val q3 = for { c <- tableQ if c.id === id } yield c.language
    db.run(q3.update(language))
    val q4 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q4.update(description))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Setting]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    db.run(action)
    tableQ.result
  }
}
