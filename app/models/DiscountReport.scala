package models

import play.api.libs.json._

case class DiscountReport(id: Long, startDate: String, endDate: String, status: String, total: Double)

object DiscountReport {
  implicit val DiscountReportFormat = Json.format[DiscountReport]
}