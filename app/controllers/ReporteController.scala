package controllers

import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ Future, ExecutionContext, Await }

import be.objectify.deadbolt.scala.DeadboltActions
import security.MyDeadboltHandler
import javax.inject._

class ReportController @Inject() (deadbolt: DeadboltActions, repo:ReportRepository, repoAccount: AccountRepository, repoTransDetails: TransactionDetailRepository, repoTrans: TransactionRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val newForm: Form[CreateReportForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "account" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140)))(CreateReportForm.apply)(CreateReportForm.unapply)
  }

  def index = deadbolt.WithAuthRequest()()  { implicit request =>
    Future { Ok(views.html.reporte_index(new MyDeadboltHandler)(request, messagesApi.preferred(request))) }
  }

  def balance = deadbolt.WithAuthRequest()()  { implicit request =>
    val activos = getByActivo()
    val pasivos = getByPasivo()
    val patrimonios = getByPatrimonio()
    Future { Ok(views.html.reporte_balance(new MyDeadboltHandler, activos, pasivos, patrimonios)(request, messagesApi.preferred(request))) }
  }

  def diary = deadbolt.WithAuthRequest()()  { implicit request =>
    val transactions = getTransactions()
    transactions.foreach { transaction =>
      Await.result(repoTransDetails.listByTransaction(transaction.id).map {
        res =>
          print(res)
          transaction.details = res;
      }, 500.millis)
    }
    Future { Ok(views.html.reporte_diary(new MyDeadboltHandler, transactions)(request, messagesApi.preferred(request))) }
  }

  def mayor = deadbolt.WithAuthRequest()()  { implicit request =>
    val details = getTransactionDetails()
    Future { Ok(views.html.reporte_mayor(new MyDeadboltHandler, details)) }
  }

  def sumasYSaldos = deadbolt.WithAuthRequest()()  { implicit request =>
    val accounts = getChilAccounts()
    Future { Ok(views.html.reporte_sumasYSaldos(new MyDeadboltHandler, accounts)(request, messagesApi.preferred(request))) }
  }

  //  RFB = 510 - 410
  //  ROB = RFB + 540 - 440
  //  RODI = ROB + 530 - 430
  //  RON = RODI - 450
  //  RDADC = RON - ADADC
  //  RNEGA = RDADC + 570 - 470
  //  RAIYI = RNEGA + 580 - 480
  //  RAI = RAIYI - ACEI
  //  RNG = RAI - 460

  def resultFinance = deadbolt.WithAuthRequest()()  { implicit request =>
    val account510: Seq[Account] = getAccountByCode("510")
    val account410: Seq[Account] = getAccountByCode("410")
    val account540: Seq[Account] = getAccountByCode("540")
    val account440: Seq[Account] = getAccountByCode("440")
    val account530: Seq[Account] = getAccountByCode("530")
    val account430: Seq[Account] = getAccountByCode("430")
    val accountADADC: Seq[Account] = getAccountByCode("ADADC") // ADADC
    val account450: Seq[Account] = getAccountByCode("450")
    val account570: Seq[Account] = getAccountByCode("570")
    val account470: Seq[Account] = getAccountByCode("470")
    val account580: Seq[Account] = getAccountByCode("580")
    val account480: Seq[Account] = getAccountByCode("480")
    val accountACEI: Seq[Account] = getAccountByCode("ACEI") // ACEI
    val account460: Seq[Account] = getAccountByCode("460")

    var hello: String = ""
    if (account510.length > 0 && account410.length > 0 && account540.length > 0 && account440.length > 0
      && account530.length > 0 && account430.length > 0 && accountADADC.length > 0 && account450.length > 0
      && account570.length > 0 && account470.length > 0 && account580.length > 0 && account480.length > 0 &&
      accountACEI.length > 0 && account460.length > 0) {
      Future { Ok(views.html.report_result(new MyDeadboltHandler, account510(0), account410(0), account540(0), account440(0), account530(0),
        account430(0), accountADADC(0), account450(0), account570(0), account470(0), account580(0), account480(0),
        accountACEI(0), account460(0))(request, messagesApi.preferred(request))) }
    } else {
      hello = "It is not complete"
      Future { Ok("This is an example: " + hello) }
    }
  }

  def resultFinance1 = deadbolt.WithAuthRequest()()  { implicit request =>
    val account510: Seq[Account] = getAccountByCode("510")
    val account410: Seq[Account] = getAccountByCode("410")
    val account540: Seq[Account] = getAccountByCode("540")
    val account440: Seq[Account] = getAccountByCode("440")
    val account530: Seq[Account] = getAccountByCode("530")
    val account430: Seq[Account] = getAccountByCode("430")
    val accountADADC: Seq[Account] = getAccountByCode("ADADC") // ADADC
    val account450: Seq[Account] = getAccountByCode("450")
    val account570: Seq[Account] = getAccountByCode("570")
    val account470: Seq[Account] = getAccountByCode("470")
    val account580: Seq[Account] = getAccountByCode("580")
    val account480: Seq[Account] = getAccountByCode("480")
    val accountACEI: Seq[Account] = getAccountByCode("ACEI") // ACEI
    val account460: Seq[Account] = getAccountByCode("460")

    var hello: String = ""
    if (account510.length > 0 && account410.length > 0 && account540.length > 0 && account440.length > 0
      && account530.length > 0 && account430.length > 0 && accountADADC.length > 0 && account450.length > 0
      && account570.length > 0 && account470.length > 0 && account580.length > 0 && account480.length > 0 &&
      accountACEI.length > 0 && account460.length > 0) {
      Future { Ok(views.html.report_result(new MyDeadboltHandler, account510(0), account410(0), account540(0), account440(0), account530(0),
        account430(0), accountADADC(0), account450(0), account570(0), account470(0), account580(0), account480(0),
        accountACEI(0), account460(0))) }
    } else {
      hello = "It is not complete"
      Future { Ok("This is an example: " + hello) }
    }
  }

  def getAccountByCode(code: String): Seq[Account] = {
    Await.result(repoAccount.getByCode(code).map {
      res => res;
    }, 500.millis)
  }

  def getTransactionDetails(): Seq[TransactionDetail] = {
    Await.result(repoTransDetails.list().map {
      res => res;
    }, 1000.millis)
  }

  def getTransactions(): Seq[Transaction] = {
    Await.result(repoTrans.getListPopulated().map {
      res => res
    }, 1000.millis)
  }

  def getByActivo(): Seq[Account] = {
    Await.result(repoAccount.getByActivo().map {
      res => res;
    }, 1000.millis)
  }

  def getChilAccounts(): Seq[Account] = {
    Await.result(repoAccount.getChilAccounts().map {
      res => res;
    }, 1000.millis)
  }

  def getByPasivo(): Seq[Account] = {
    Await.result(repoAccount.getByPasivo().map {
      res => res;
    }, 1000.millis)
  }

  def getByPatrimonio(): Seq[Account] = {
    Await.result(repoAccount.getByPatrimonio().map {
      res => res;
    }, 1000.millis)
  }

  def addReport = deadbolt.WithAuthRequest()() { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.reporte_index(new MyDeadboltHandler)(request, messagesApi.preferred(request))))
      },
      reporte => {
        repo.create(reporte.monto, reporte.account, reporte.cliente).map { _ =>
          Redirect(routes.ReportController.index)
        }
      })
  }

  def getReports = deadbolt.WithAuthRequest()() { request =>
    repo.list().map { reportes =>
      Ok(Json.toJson(reportes))
    }
  }
}

case class CreateReportForm(monto: Int, account: Int, cliente: Int)
