package models

import play.api.libs.json._

case class Account(id: Long, code: String, name: String, type_1: String, negativo: String, parent: Long, description: String, child: Boolean, debit: Double, credit: Double)

object Account {
  implicit val AccountFormat = Json.format[Account]
}
