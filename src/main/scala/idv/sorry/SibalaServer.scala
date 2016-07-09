package idv.sorry

import scala.io.StdIn

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow

object SibalaServer extends SibalaService {
	def main(args: Array[String]) {
		println("Sibala!")

		implicit val executionContext = casino.dispatcher	// required for flatMap/onComplete
		
		val bindingFuture = Http().bindAndHandle(route, "localhost", 8000)

		println("Listening http://localhost:8000/\nPress Enter to stop")
		StdIn.readLine()
		bindingFuture
			.flatMap(_.unbind()) // unbind port
			.onComplete(_ => {
					casino.terminate()	// shutdown system
					println("Sayonara")
				})
	}
}
