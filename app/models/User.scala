package models
import be.objectify.deadbolt.scala.models.Subject
import play.libs.Scala

import play.api.libs.json._


import be.objectify.deadbolt.scala.models.Subject

/**
  *
  * @author Steve Chaloner (steve@objectify.be)
  */
case class User(val id: Long, val name: String, val carnet: Int, val phone: Int, val address: String, val Salary: Int, val type_1: String, val login: String, val password: String) extends Subject {
  override def roles: List[SecurityRole] =
    List(SecurityRole("foo"),
         SecurityRole("bar"))

  override def permissions: List[UserPermission] =
    List(UserPermission("printers.edit"))

  override def identifier: String = name
}
/*
case class User(id: Long, name: String, carnet: Int, phone: Int, address: String, Salary: Int, type_1: String, login: String, password: String) extends Subject {
  def getRoles: java.util.List[SecurityRole] = {
    Scala.asJava(List(new SecurityRole("foo"),
      new SecurityRole("bar")))
  }

  def getPermissions: java.util.List[UserPermission] = {
    Scala.asJava(List(new UserPermission("printers.edit")))
  }

  def getIdentifier: String = name
}
*/

object User {
  implicit val UserFormat = Json.format[User]
}
