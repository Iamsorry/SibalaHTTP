package idv.sorry

object Dice {
	val dice = new scala.util.Random
	def Roll() = dice.nextInt(10) + 3
}