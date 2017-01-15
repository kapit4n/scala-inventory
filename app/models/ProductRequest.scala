package models

import play.api.libs.json._

case class ProductRequest(
  id: Long, date: String, employee: Long, employeeName: String,
  storekeeper: Long, storekeeperName: String, status: String,
  detail: String, type_1: String, userId: Long, userName: String)

object ProductRequest {
  implicit val ProductRequestFormat = Json.format[ProductRequest]
}