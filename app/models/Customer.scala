package models

import play.api.libs.json._

case class Customer(
  id: Long, name: String, carnet: Int, phone: Int,
  address: String, account: String,
  companyName: String, totalDebt: Double)

object Customer {
  implicit val customerFormat = Json.format[Customer]
}
