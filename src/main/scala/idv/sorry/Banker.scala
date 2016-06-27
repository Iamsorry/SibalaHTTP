package idv.sorry

import akka.actor.{ Actor, ActorRef }

class Banker(val name: String) extends Actor {
	def receive: Actor.Receive = {

		case Compete(playerName, playerPoint) =>
			val bankerPoint = Dice.Roll()
			sender ! Result(if (playerPoint > bankerPoint) "Sausage" else "Nosu")

	}
}