package models

import play.libs.Scala
import be.objectify.deadbolt.core.models.Subject

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

class UserSecurity(val userName: String, val rolParam: String, var rolesListParam: List[SecurityRole] = List()) extends Subject {
  var rol = rolParam

  var rolesList: Map[String, List[SecurityRole]] = Map("admin" ->
    List(
      new SecurityRole("role"), new SecurityRole("roleCreate"), new SecurityRole("roleList"), new SecurityRole("roleShow"), new SecurityRole("roleEdit"), new SecurityRole("roleDelete"),
      new SecurityRole("measure"), new SecurityRole("measureCreate"), new SecurityRole("measureList"), new SecurityRole("measureShow"), new SecurityRole("measureEdit"), new SecurityRole("measureDelete"),
      new SecurityRole("product"), new SecurityRole("productCreate"), new SecurityRole("productList"), new SecurityRole("productShow"), new SecurityRole("productEdit"), new SecurityRole("productDelete"),
      new SecurityRole("vendor"), new SecurityRole("vendorCreate"), new SecurityRole("vendorList"), new SecurityRole("vendorShow"), new SecurityRole("vendorEdit"), new SecurityRole("vendorDelete"),
      new SecurityRole("customer"), new SecurityRole("customerCreate"), new SecurityRole("customerList"), new SecurityRole("customerShow"), new SecurityRole("customerEdit"), new SecurityRole("customerDelete"),
      new SecurityRole("company"), new SecurityRole("companyXXXCreate"), new SecurityRole("companyXXXList"), new SecurityRole("companyShow"), new SecurityRole("companyXXXEdit"), new SecurityRole("companyXXXDelete"),
      new SecurityRole("user"), new SecurityRole("userCreate"), new SecurityRole("userList"), new SecurityRole("userShow"), new SecurityRole("userEdit"), new SecurityRole("userDelete"),
      new SecurityRole("account"), new SecurityRole("accountCreate"), new SecurityRole("accountList"), new SecurityRole("accountShow"), new SecurityRole("accountEdit"), new SecurityRole("accountDelete"),
      new SecurityRole("transaction"), new SecurityRole("transactionCreate"), new SecurityRole("transactionList"), new SecurityRole("transactionShow"), new SecurityRole("transactionEdit"), new SecurityRole("transactionDelete"),
      new SecurityRole("transactionDetail"), new SecurityRole("transactionDetailCreate"), new SecurityRole("transactionDetailList"), new SecurityRole("transactionDetailShow"), new SecurityRole("transactionDetailEdit"), new SecurityRole("transactionDetailDelete"),
      new SecurityRole("productRequest"), new SecurityRole("productRequestCreate"), new SecurityRole("productRequestList"), new SecurityRole("productRequestShow"), new SecurityRole("productRequestEdit"), new SecurityRole("productRequestDelete"), new SecurityRole("productRequestSend"), new SecurityRole("productRequestAccept"), new SecurityRole("productRequestFinish"),
      new SecurityRole("productRequestByInsumo"), new SecurityRole("productRequestByInsumoCreate"), new SecurityRole("productRequestByInsumoList"), new SecurityRole("productRequestByInsumoShow"), new SecurityRole("productRequestByInsumoEdit"), new SecurityRole("productRequestByInsumoDelete"), new SecurityRole("productRequestByInsumoSend"), new SecurityRole("productRequestByInsumoAccept"), new SecurityRole("productRequestByInsumoFinish"),
      new SecurityRole("requestRow"), new SecurityRole("requestRowCreate"), new SecurityRole("requestRowList"), new SecurityRole("requestRowShow"), new SecurityRole("requestRowEdit"), new SecurityRole("requestRowDelete"),
      new SecurityRole("requestRowByInsumo"), new SecurityRole("requestRowByInsumoCreate"), new SecurityRole("requestRowByInsumoList"), new SecurityRole("requestRowByInsumoShow"), new SecurityRole("requestRowByInsumoEdit"), new SecurityRole("requestRowByInsumoDelete"),
      new SecurityRole("requestRowCustomer"), new SecurityRole("requestRowCustomerCreate"), new SecurityRole("requestRowCustomerList"), new SecurityRole("requestRowCustomerShow"), new SecurityRole("requestRowCustomerEdit"), new SecurityRole("requestRowCustomerDelete"),
      new SecurityRole("discountReport"), new SecurityRole("discountReportCreate"), new SecurityRole("discountReportList"), new SecurityRole("discountReportShow"), new SecurityRole("discountReportEdit"), new SecurityRole("discountReportDelete"), new SecurityRole("discountReportFinalize"),
      new SecurityRole("discountDetail"), new SecurityRole("discountDetailCreate"), new SecurityRole("discountDetailList"), new SecurityRole("discountDetailShow"), new SecurityRole("discountDetailEdit"), new SecurityRole("discountDetailDelete"),
      new SecurityRole("productInv"), new SecurityRole("productInvCreate"), new SecurityRole("productInvList"), new SecurityRole("productInvShow"), new SecurityRole("productInvEdit"), new SecurityRole("productInvDelete"),
      new SecurityRole("report"), new SecurityRole("balanceShow"), new SecurityRole("diaryShow"), new SecurityRole("financeShow"), new SecurityRole("mayorShow"), new SecurityRole("sumasSaldosShow"),
      new SecurityRole("setting"), new SecurityRole("settingShow"), new SecurityRole("settingEdit"),
      new SecurityRole("logEntry"), new SecurityRole("logEntryShow"), new SecurityRole("logEntryList")), "employee" ->
    List(
      new SecurityRole("productRequest"), new SecurityRole("productRequestCreate"), new SecurityRole("productRequestList"),
      new SecurityRole("productRequestShow"), new SecurityRole("productRequestEdit"), new SecurityRole("productRequestDelete"),
      new SecurityRole("productRequestSend"),
      new SecurityRole("requestRow"), new SecurityRole("requestRowCreate"), new SecurityRole("requestRowList"), new SecurityRole("requestRowShow"), new SecurityRole("requestRowEdit"), new SecurityRole("requestRowDelete"),
      new SecurityRole("requestRowCustomer"), new SecurityRole("requestRowCustomerCreate"), new SecurityRole("requestRowCustomerList"), new SecurityRole("requestRowCustomerShow"), new SecurityRole("requestRowCustomerEdit"), new SecurityRole("requestRowCustomerDelete"),
      new SecurityRole("customer"), new SecurityRole("customerList"), new SecurityRole("customerShow"),
      new SecurityRole("product"), new SecurityRole("productList"), new SecurityRole("productShow")), "store" ->
    List(
      new SecurityRole("measure"), new SecurityRole("measureCreate"), new SecurityRole("measureList"), new SecurityRole("measureShow"), new SecurityRole("measureEdit"), new SecurityRole("measureDelete"),
      new SecurityRole("product"), new SecurityRole("productCreate"), new SecurityRole("productList"), new SecurityRole("productShow"), new SecurityRole("productEdit"), new SecurityRole("productDelete"),
      new SecurityRole("vendor"), new SecurityRole("vendorCreate"), new SecurityRole("vendorList"), new SecurityRole("vendorShow"), new SecurityRole("vendorEdit"), new SecurityRole("vendorDelete"),
      new SecurityRole("customer"), new SecurityRole("customerCreate"), new SecurityRole("customerList"), new SecurityRole("customerShow"), new SecurityRole("customerEdit"), new SecurityRole("customerDelete"),
      new SecurityRole("user"), new SecurityRole("userCreate"), new SecurityRole("userList"), new SecurityRole("userShow"), new SecurityRole("userEdit"), new SecurityRole("userDelete"),
      new SecurityRole("transaction"), new SecurityRole("transactionCreate"), new SecurityRole("transactionList"), new SecurityRole("transactionShow"), new SecurityRole("transactionEdit"), new SecurityRole("transactionDelete"),
      new SecurityRole("transactionDetail"), new SecurityRole("transactionDetailCreate"), new SecurityRole("transactionDetailList"), new SecurityRole("transactionDetailShow"), new SecurityRole("transactionDetailEdit"), new SecurityRole("transactionDetailDelete"),
      new SecurityRole("productRequest"), new SecurityRole("productRequestCreate"), new SecurityRole("productRequestList"), new SecurityRole("productRequestShow"), new SecurityRole("productRequestEdit"), new SecurityRole("productRequestDelete"), new SecurityRole("productRequestSend"), new SecurityRole("productRequestAccept"), new SecurityRole("productRequestFinish"),
      new SecurityRole("requestRow"), new SecurityRole("requestRowCreate"), new SecurityRole("requestRowList"), new SecurityRole("requestRowShow"), new SecurityRole("requestRowEdit"), new SecurityRole("requestRowDelete"),
      new SecurityRole("requestRowCustomer"), new SecurityRole("requestRowCustomerCreate"), new SecurityRole("requestRowCustomerList"), new SecurityRole("requestRowCustomerShow"), new SecurityRole("requestRowCustomerEdit"), new SecurityRole("requestRowCustomerDelete"),
      new SecurityRole("discountReport"), new SecurityRole("discountReportCreate"), new SecurityRole("discountReportList"), new SecurityRole("discountReportShow"), new SecurityRole("discountReportEdit"), new SecurityRole("discountReportDelete"), new SecurityRole("discountReportFinalize"),
      new SecurityRole("discountDetail"), new SecurityRole("discountDetailCreate"), new SecurityRole("discountDetailList"), new SecurityRole("discountDetailShow"), new SecurityRole("discountDetailEdit"), new SecurityRole("discountDetailDelete"),
      new SecurityRole("productInv"), new SecurityRole("productInvCreate"), new SecurityRole("productInvList"), new SecurityRole("productInvShow"), new SecurityRole("productInvEdit"), new SecurityRole("productInvDelete"),
      new SecurityRole("setting"), new SecurityRole("settingShow")), "contabilidad" ->
    List(
      new SecurityRole("transaction"), new SecurityRole("transactionCreate"), new SecurityRole("transactionList"), new SecurityRole("transactionShow"), new SecurityRole("transactionEdit"), new SecurityRole("transactionDelete"),
      new SecurityRole("transactionDetail"), new SecurityRole("transactionDetailCreate"), new SecurityRole("transactionDetailList"), new SecurityRole("transactionDetailShow"), new SecurityRole("transactionDetailEdit"), new SecurityRole("transactionDetailDelete"),
      new SecurityRole("setting"), new SecurityRole("settingShow")), "insumo" ->
    List(
      new SecurityRole("measure"), new SecurityRole("measureCreate"), new SecurityRole("measureList"), new SecurityRole("measureShow"), new SecurityRole("measureEdit"), new SecurityRole("measureDelete"),
      new SecurityRole("product"), new SecurityRole("productCreate"), new SecurityRole("productList"), new SecurityRole("productShow"), new SecurityRole("productEdit"), new SecurityRole("productDelete"),
      new SecurityRole("vendor"), new SecurityRole("vendorCreate"), new SecurityRole("vendorList"), new SecurityRole("vendorShow"), new SecurityRole("vendorEdit"), new SecurityRole("vendorDelete"),
      new SecurityRole("customer"), new SecurityRole("customerCreate"), new SecurityRole("customerList"), new SecurityRole("customerShow"), new SecurityRole("customerEdit"), new SecurityRole("customerDelete"),
      new SecurityRole("user"), new SecurityRole("userList"), new SecurityRole("userShow"),
      new SecurityRole("transaction"), new SecurityRole("transactionCreate"), new SecurityRole("transactionList"), new SecurityRole("transactionShow"), new SecurityRole("transactionEdit"), new SecurityRole("transactionDelete"),
      new SecurityRole("transactionDetail"), new SecurityRole("transactionDetailCreate"), new SecurityRole("transactionDetailList"), new SecurityRole("transactionDetailShow"), new SecurityRole("transactionDetailEdit"), new SecurityRole("transactionDetailDelete"),
      new SecurityRole("productRequest"), new SecurityRole("productRequestCreate"), new SecurityRole("productRequestList"), new SecurityRole("productRequestShow"), new SecurityRole("productRequestEdit"), new SecurityRole("productRequestDelete"),
      new SecurityRole("requestRow"), new SecurityRole("requestRowCreate"), new SecurityRole("requestRowList"), new SecurityRole("requestRowShow"), new SecurityRole("requestRowEdit"), new SecurityRole("requestRowDelete"),
      new SecurityRole("requestRowCustomer"), new SecurityRole("requestRowCustomerCreate"), new SecurityRole("requestRowCustomerList"), new SecurityRole("requestRowCustomerShow"), new SecurityRole("requestRowCustomerEdit"), new SecurityRole("requestRowCustomerDelete"),
      new SecurityRole("productInv"), new SecurityRole("productInvCreate"), new SecurityRole("productInvList"), new SecurityRole("productInvShow"), new SecurityRole("productInvEdit"), new SecurityRole("productInvDelete"),
      new SecurityRole("setting"), new SecurityRole("settingShow")))

  def getRoles: java.util.List[SecurityRole] = {
    Scala.asJava(rolesList(rol))
  }

  def getPermissions: java.util.List[UserPermission] = {
    Scala.asJava(List(new UserPermission("printers.edit")))
  }

  def getIdentifier: String = userName
}
