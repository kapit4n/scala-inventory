package models

import play.api.libs.json._

case class UserRole(id: Long, userId: Long, roleName: String, roleCode: String)
object UserRole {
  implicit val UserRoleFormat = Json.format[UserRole]
}
