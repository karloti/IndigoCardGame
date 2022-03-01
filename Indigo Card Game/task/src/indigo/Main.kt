package indigo

import indigo.Indigo.Player.*

data class Card(val rank: Rank, val suit: Suit) {
    val score = rank.score

    enum class Rank(val symbol: String, val score: Int) {
        ACE("A", 1),
        KING("K", 1),
        QUEEN("Q", 1),
        JACK("J", 1),
        TEN("10", 1),
        NINE("9", 0),
        EIGHT("8", 0),
        SEVEN("7", 0),
        SIX("6", 0),
        FIVE("5", 0),
        FOUR("4", 0),
        THREE("3", 0),
        TWO("2", 0),
    }

    enum class Suit(val symbol: Char) {
        SPADES('♠'),
        HEARTS('♥'),
        DIAMONDS('♦'),
        CLUBS('♣'),
    }

    infix fun suitOrRank(other: Card?) = rank == other?.rank || suit == other?.suit
    override fun toString() = "${rank.symbol}${suit.symbol}"

    companion object {
        val allCards = buildList {
            Suit.values().forEach { suit ->
                Rank.values().forEach { rank ->
                    add(Card(rank, suit))
                }
            }
        }.shuffled()
    }
}

sealed class Choice {
    object Exit : Choice()
    object OutOfRange : Choice()
    object InvalidNumber : Choice()
    data class Success(val card: Card) : Choice()
}

class Indigo {
    private lateinit var active: Player
    private val firstPlayer by lazy { active }

    private enum class Player(
        val hand: MutableList<Card> = mutableListOf(),
        val gain: MutableList<Card> = mutableListOf(),
        var score: Int = 0,
        var lastCardPlayed: Card? = null,
        var lastTurnTableGain: Boolean? = null,
    ) {
        HUMAN,
        COMPUTER,
        DECK(Card.allCards.toMutableList()),
        TABLE,
        GAME_OVER,
        EXIT,
        ;

        val cardInHand
            get() = when (this) {
                HUMAN -> "Cards in hand: " + hand.withIndex().joinToString(" ") { (i, card) -> "${i + 1})$card" }
                COMPUTER -> hand.joinToString(" ") { "$it" }
                DECK -> TODO()
                TABLE -> TODO()
                GAME_OVER -> TODO()
                EXIT -> TODO()
            }

        fun Card.playCard(): Boolean {
            if (!hand.contains(this)) return false
            lastCardPlayed = this
            if (suitOrRank(topCardTable)) {
                gain.addAll(TABLE.hand)
                addScore(cards = TABLE.hand)
                TABLE.hand.clear()

                gain.add(this)
                addScore(card = this)
                hand.remove(this)

                lastTableWinner = this@Player
                lastTurnTableGain = true
            } else {
                TABLE.hand.add(this)
                hand.remove(this)
                lastTurnTableGain = false
            }
            return true
        }

        fun addScore(card: Card? = null, cards: List<Card>? = null, value: Int = 0) {
            score += (card?.score ?: 0) + (cards?.sumOf { it.score } ?: 0) + value
        }

        fun getCardsFromDeck(count: Int) = if (count <= DECK.hand.size) {
            repeat(count) { hand.add(DECK.hand.removeFirst()) }
            true
        } else
            false

        fun winCardsFromTable() {
            gain.addAll(TABLE.hand)
            addScore(cards = TABLE.hand)
        }

        fun choice(): Choice {
            (((readln().uppercase()
                .takeIf { it != "EXIT" } ?: return Choice.Exit)
                .toIntOrNull() ?: return Choice.InvalidNumber)
                .takeIf { it - 1 in hand.indices } ?: return Choice.OutOfRange)
                .let { return Choice.Success(card = hand[it - 1]) }
        }

        fun candidate(): Card? {
            val candidates: List<Card> = hand.filter { it suitOrRank topCardTable }
            return when {
                hand.size < 2 -> hand.singleOrNull()
                candidates.isEmpty() -> {
                    val suitChoice: Card? = hand
                        .groupBy { it.suit }
                        .filterValues { it.size > 1 }
                        .values
                        .flatten()
                        .randomOrNull()
                    val rankChoice: Card? = hand
                        .groupBy { it.rank }
                        .filterValues { it.size > 1 }
                        .values
                        .flatten()
                        .randomOrNull()
                    suitChoice ?: rankChoice ?: hand.randomOrNull()
                }
                candidates.size == 1 -> candidates.first()
                else -> {
                    val suitChoice: Card? = hand
                        .filter { it.suit == topCardTable?.suit }
                        .takeIf { it.size > 1 }
                        ?.random()

                    val rankChoice: Card? = hand
                        .filter { it.rank == topCardTable?.rank }
                        .takeIf { it.size > 1 }
                        ?.random()
                    suitChoice ?: rankChoice ?: candidates.randomOrNull()
                }
            }
        }

        fun play(): Player {
            when (this) {
                HUMAN -> while (true) {
                    println("Choose a card to play (1-${hand.size}):")
                    when (val choice = HUMAN.choice()) {
                        Choice.InvalidNumber -> continue
                        Choice.OutOfRange -> continue
                        Choice.Exit -> return EXIT
                        is Choice.Success -> {
                            choice.card.playCard()
                            if (lastTurnTableGain!!) {
                                println("Player wins cards")
                                println(results())
                            }
                            return COMPUTER
                        }
                    }
                }
                COMPUTER -> {
                    candidate()?.also { it.playCard() }
                    println("Computer plays $lastCardPlayed")
                    if (lastTurnTableGain!!) {
                        println("Computer wins cards")
                        println(results())
                    }
                    return HUMAN
                }
                DECK -> TODO()
                TABLE -> TODO()
                GAME_OVER -> TODO()
                EXIT -> TODO()
            }
        }

        override fun toString() = when (this) {
            DECK -> super.toString()
            TABLE -> if (hand.isEmpty()) "\nNo cards on the table" else "\n${hand.size} cards on the table, and the top card is $topCardTable"
            GAME_OVER -> "Game Over"
            EXIT -> "Game Over"
            HUMAN -> TODO()
            COMPUTER -> TODO()
        }

        companion object {
            var lastTableWinner: Player? = null
            val topCardTable get() = TABLE.hand.lastOrNull()

            fun results() = """
                Score: Player %s - Computer %s
                Cards: Player %s - Computer %s
                """.trimIndent().format(
                HUMAN.score,
                COMPUTER.score,
                HUMAN.gain.size,
                COMPUTER.gain.size,
            )
        }
    }

    init {
        println("Indigo Card Game")
        while (!::active.isInitialized) {
            println("Play first?")
            when (readln().uppercase()) {
                "YES" -> active = HUMAN
                "NO" -> active = COMPUTER
            }
        }
        TABLE.getCardsFromDeck(count = 4)
        println("Initial cards on the table: " + TABLE.hand.joinToString(" "))
        while (true) {
            if (HUMAN.hand.isEmpty() && COMPUTER.hand.isEmpty() &&
                !(HUMAN.getCardsFromDeck(count = 6) && COMPUTER.getCardsFromDeck(count = 6))
            ) active = GAME_OVER

            when (active) {
                HUMAN -> {
                    println(TABLE)
                    println(HUMAN.cardInHand)
                    active = HUMAN.play()
                }
                COMPUTER -> {
                    println(TABLE)
                    println(COMPUTER.cardInHand)
                    active = COMPUTER.play()
                }
                GAME_OVER -> {
                    println(TABLE)
                    (Player.lastTableWinner ?: firstPlayer).apply { winCardsFromTable() }
                    when {
                        HUMAN.gain.size > COMPUTER.gain.size -> HUMAN
                        COMPUTER.gain.size > HUMAN.gain.size -> COMPUTER
                        else -> firstPlayer
                    }.apply { score += 3 }
                    println(Player.results())
                    println(GAME_OVER)
                    break
                }
                EXIT -> {
                    println(EXIT)
                    break
                }
                DECK -> TODO()
                TABLE -> TODO()
            }
        }
    }
}

fun main() {
    Indigo()
}