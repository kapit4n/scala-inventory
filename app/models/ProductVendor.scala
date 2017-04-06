package models

import play.api.libs.json._

case class ProductVendor(id: Long, productId: Long, vendorId: Long)
object ProductVendor {
  implicit val ProductVendorFormat = Json.format[ProductVendor]
}
