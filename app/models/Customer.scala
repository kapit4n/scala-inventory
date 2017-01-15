package models

import play.api.libs.json._

case class Customer(
  id: Long, name: String, carnet: Int, telefono: Int,
  direccion: String, account: String,
  companyName: String, totalDebt: Double,
  numberPayment: Int, position: String)

object Customer {
  implicit val customerFormat = Json.format[Customer]
}
