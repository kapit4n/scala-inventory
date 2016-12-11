package models

import play.api.libs.json._

case class Module(
  id: Long, name: String, president: String, description: String,
  companyId: Long, companyName: String)
object Module {
  implicit val ModuleFormat = Json.format[Module]
}
