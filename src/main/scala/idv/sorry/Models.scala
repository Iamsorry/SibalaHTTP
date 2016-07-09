package idv.sorry

import spray.json.DefaultJsonProtocol._

object Models {
	case class Player(playerName: String)
	object JsonFormats {
		implicit val playerFormat = jsonFormat1(Player)
		implicit val resultFormat = jsonFormat1(Result)
	}
}