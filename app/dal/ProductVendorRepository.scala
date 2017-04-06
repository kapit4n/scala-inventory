package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.ProductVendor
import models.Roles

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductVendorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProductVendorsTable(tag: Tag) extends Table[ProductVendor](tag, "productVendor") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def productId = column[Long]("productId")
    def vendorId = column[Long]("vendorId")
    def * = (id, productId, vendorId) <> ((ProductVendor.apply _).tupled, ProductVendor.unapply)
  }

  private val tableProductVendor = TableQuery[ProductVendorsTable]

  def createProductVendor(productId: Long, vendorId: Long): Future[ProductVendor] = db.run {
    (tableProductVendor.map(p => (p.productId, p.vendorId))
      returning tableProductVendor.map(_.id)
      into ((nameAge, id) => ProductVendor(id, nameAge._1, nameAge._2))) += (productId, vendorId)
  }

  def listVendorsByProductId(productId: Long): Future[Seq[ProductVendor]] = db.run {
    tableProductVendor.filter(_.productId === productId).result
  }

  // to cpy
  def getProductVendorById(id: Long): Future[Seq[ProductVendor]] = db.run {
    tableProductVendor.filter(_.id === id).result
  }

  // delete required
  def deleteProductVendor(id: Long): Future[Seq[ProductVendor]] = db.run {
    val q = tableProductVendor.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    tableProductVendor.result
  }
}
