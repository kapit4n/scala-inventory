package controllers

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._

class ClienteController @Inject() (repo: ClienteRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  /**
   * The mapping for the person form.
   */
  val clienteForm: Form[CreateClienteForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "id_company" -> number.verifying(min(0), max(140)))(CreateClienteForm.apply)(CreateClienteForm.unapply)
  }

  /**
   * The index action.
   */
  def cliente_list = LanguageAction {
    Ok(views.html.cliente_index(clienteForm))
  }

  /**
   * The index action.
   */
  def cliente_doctor = LanguageAction {
    Ok(views.html.cliente_doctor(clienteForm))
  }

  /**
   * A REST endpoint that gets all the clientes as JSON.
   */
  def getClientes = LanguageAction.async {
    repo.list().map { clientes =>
      Ok(Json.toJson(clientes))
    }
  }
}

/**
 * The create person form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreateClienteForm(name: String, carnet: Int, id_company: Int)
