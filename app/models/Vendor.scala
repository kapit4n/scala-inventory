package models

import play.api.libs.json._

case class Vendor(id: Long, name: String, phone: Int, address: String, contact: String, account: Long)

object Vendor {
  implicit val vendorFormat = Json.format[Vendor]
}
