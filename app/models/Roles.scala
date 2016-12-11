package models

import play.api.libs.json._

case class Roles(id: Long, roleName: String, roleCode: String)
object Roles {
  implicit val RolesFormat = Json.format[Roles]
}
