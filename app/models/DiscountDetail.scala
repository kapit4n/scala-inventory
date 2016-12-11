package models

import play.api.libs.json._

case class DiscountDetail(
  id: Long, discountReport: Long, customerId: Long,
  customerName: String, status: String,
  discount: Double, requestRow: Long)

object DiscountDetail {
  implicit val ReportDiscountFormat = Json.format[DiscountDetail]
}