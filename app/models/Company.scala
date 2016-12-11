package models

import play.api.libs.json._

case class Company(id: Long, name: String)

object Company {
  implicit val companyFormat = Json.format[Company]
}
