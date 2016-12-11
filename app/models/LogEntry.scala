package models

import play.api.libs.json._

case class LogEntry(id: Long, action: String, tableName1: String, userId: Long, userName: String, description: String)

object LogEntry {
  implicit val LogEntryFormat = Json.format[LogEntry]
}
