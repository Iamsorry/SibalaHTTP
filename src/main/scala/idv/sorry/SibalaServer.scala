package idv.sorry

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directive._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.DefaultJsonProtocol.jsonFormat1

object SibalaServer {
	def main(args: Array[String]) {
		println("Sibala!")

		implicit val casino = ActorSystem("bicycle")

		val banker = casino.actorOf(Props(classOf[Banker], "abeh"))
		
		implicit val materializer = ActorMaterializer()
		implicit val executionContext = casino.dispatcher	// required for flatMap/onComplete

		case class Player(playerName: String)
		implicit val playerFormat = jsonFormat1(Player)
		implicit val resultFormat = jsonFormat1(Result)

		val route =
			pathSingleSlash {
				encodeResponse {
					getFromFile("src/main/resources/index.html")
				}
			} ~
			path("test") {
				get {
					complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "GET test"))
				}
			} ~
			path("play") {
				post {
					/*
					val playerName = formField("playerName")	// use form post
					*/
					entity(as[Player]) { player =>	// use json post
						val playerPoint = Dice.Roll()
	
						implicit val timeout = Timeout(5 seconds)

						/* blocking
						val future = banker ? Compete(player.playerName.toString(), playerPoint)
						val blockingResult = Await.result(future, timeout.duration).asInstanceOf[Result]
						complete(blockingResult)
						*/
						
						// non-blocking
						val result: Future[Result] = ask(banker, Compete(player.playerName.toString(), playerPoint)).mapTo[Result]
						onComplete(result) { done => complete(result) }
					}
				}
			}

		val bindingFuture = Http().bindAndHandle(route, "localhost", 8000)

		println("Listening http://localhost:8000/\nPress Enter to stop")
		StdIn.readLine()
		bindingFuture
			.flatMap(_.unbind()) // unbind port
			.onComplete(_ => {
					casino.terminate()	// shutdown system
					println("Terminated")
				})
	}
}
