package models

import play.api.libs.json._

case class Report(id: Long, monto: Int, account: Int, cliente: Int)

object Report {
  implicit val reporteFormat = Json.format[Report]
}