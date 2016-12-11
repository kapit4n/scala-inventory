package models

import play.api.libs.json._

case class Measure(id: Long, name: String, quantity: Double, description: String, measureId: Long, measureName: String)

object Measure {
  implicit val MeasureFormat = Json.format[Measure]
}
