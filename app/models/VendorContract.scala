package models

import play.api.libs.json._

case class VendorContract(id: Long, vendorId: Long, startDate: String, endDate: String)

object VendorContract {
  implicit val vendorContractFormat = Json.format[VendorContract]
}
