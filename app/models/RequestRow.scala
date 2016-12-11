package models

import play.api.libs.json._
// I will need to remove the customerId
case class RequestRow(
  id: Long, requestId: Long, productId: Long, productName: String, quantity: Int,
  price: Double, totalPrice: Double, paid: Double, credit: Double, paidDriver: Double,
  creditDriver: Double, status: String, measureId: Long, measureName: String)

object RequestRow {
  implicit val RequestRowFormat = Json.format[RequestRow]
}