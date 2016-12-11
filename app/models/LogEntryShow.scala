package models

import play.api.libs.json._

case class LogEntryShow(id: Long, action: String, tableName1: String, userId: Long, userName: String, description: String, createdAt: String)

object LogEntryShow {
  implicit val LogEntryFormat = Json.format[LogEntryShow]
}
