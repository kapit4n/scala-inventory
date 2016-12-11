package models

import play.api.libs.json._

case class Vendor(id: Long, name: String, telefono: Int, direccion: String, contacto: String, account: Long)

object Vendor {
  implicit val vendorFormat = Json.format[Vendor]
}