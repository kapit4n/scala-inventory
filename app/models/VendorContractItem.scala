package models

import play.api.libs.json._
import java.util.Date

case class VendorContractItem(id: Long, contractId: Int, productId: Int, startDate: Date, endDate: Date, cost: Double)

object VendorContractItem {
  implicit val vendorContractItemFormat = Json.format[VendorContractItem]
}
