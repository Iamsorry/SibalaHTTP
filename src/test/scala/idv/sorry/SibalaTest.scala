package idv.sorry

import scala.concurrent.duration.DurationInt

import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.http.scaladsl.server._
import akka.http.scaladsl.model._
import akka.http.scaladsl.settings.RoutingSettings
import akka.http.scaladsl.server.RoutingLog

import idv.sorry.Models.JsonFormats._

class SibalaTest extends WordSpec
	with Matchers
	with ScalatestRouteTest
	with SibalaService {

	override implicit val materializer = super[SibalaService].materializer
	
	/*
	 * injectIntoRoute is needed such that the compiler can find the correct TildeArrow instance (~>)
	 * while injectIntoRoute requires a whole bunch of other implicits
	 */
	implicit val timeout = RouteTestTimeout(5 seconds)
	implicit val routingSettings = RoutingSettings.default(casino)
	implicit val routingLog = RoutingLog.fromActorSystem(casino)
	
	"Sibala service" should {

		val playRequest = HttpRequest(
			HttpMethods.POST,
			uri = "/play",
			entity = HttpEntity(MediaTypes.`application/json`, """{"playerName": "adia"}""")
		)
		
		"return an HTML page for GET requests to the root path" in {
			Get("/").~>(route)(TildeArrow.injectIntoRoute) ~> check {
				responseAs[String] should include("<title>SibalaHTTP</title>")
			}
		}

		"return a simple string for GET requests to the test request" in {
			Get("/test").~>(route)(TildeArrow.injectIntoRoute) ~> check {
				responseAs[String] shouldEqual "GET test"
			}
		}

		"return a result of 'Sausage' or 'Nosu' for the play request" in {
			playRequest.~>(route)(TildeArrow.injectIntoRoute) ~> check {
				responseAs[Result].result should (equal("Sausage") or equal("Nosu"))
			}
		}

	}
}