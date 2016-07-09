package idv.sorry

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonMarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directive._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import idv.sorry.Models._
import idv.sorry.Models.JsonFormats._

trait SibalaService {
	implicit val casino = ActorSystem("bicycle")
	val banker = casino.actorOf(Props(classOf[Banker], "abeh"))

	implicit def materializer = ActorMaterializer()
	
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
}
