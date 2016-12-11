package models

import play.api.libs.json._

case class Setting(id: Long, name: String, president: String, description: String)

object Setting {
  implicit val SettingFormat = Json.format[Setting]
}
