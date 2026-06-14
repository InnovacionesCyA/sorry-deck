package com.cavacollective.sorrydeck

/**
 * A single Sorry!-style card.
 *
 * @param label   What's shown big on the card face ("1", "2", ..., "SORRY!").
 * @param title   Short heading on the card body.
 * @param rule    Full canonical rule text shown below the number.
 * @param isSorry True for the four "Sorry!" cards.
 */
data class Card(
    val label: String,
    val title: String,
    val rule: String,
    val isSorry: Boolean = false
)

object Deck {

    // Canonical Sorry! distribution: 45 cards, no 6 and no 9.
    // 1 x5, 2 x4, 3 x4, 4 x4, 5 x4, 7 x4, 8 x4, 10 x4, 11 x4, 12 x4, Sorry! x4
    private val templates: List<Pair<Card, Int>> = listOf(
        Card(
            label = "1",
            title = "Move 1",
            rule = "Move a pawn from Start, or move one pawn 1 space forward."
        ) to 5,
        Card(
            label = "2",
            title = "Move 2 — Draw Again",
            rule = "Move a pawn from Start, or move one pawn 2 spaces forward. DRAW AGAIN."
        ) to 4,
        Card(
            label = "3",
            title = "Move 3",
            rule = "Move one pawn 3 spaces forward."
        ) to 4,
        Card(
            label = "4",
            title = "Move 4 Backward",
            rule = "Move one pawn 4 spaces BACKWARD."
        ) to 4,
        Card(
            label = "5",
            title = "Move 5",
            rule = "Move one pawn 5 spaces forward."
        ) to 4,
        Card(
            label = "7",
            title = "Move 7 or Split",
            rule = "Move one pawn 7 spaces forward, OR split the 7 between two pawns."
        ) to 4,
        Card(
            label = "8",
            title = "Move 8",
            rule = "Move one pawn 8 spaces forward."
        ) to 4,
        Card(
            label = "10",
            title = "Move 10 or Back 1",
            rule = "Move one pawn 10 spaces forward, OR move one pawn 1 space backward."
        ) to 4,
        Card(
            label = "11",
            title = "Move 11 or Switch",
            rule = "Move one pawn 11 spaces forward, OR switch places with an opponent's pawn on the track."
        ) to 4,
        Card(
            label = "12",
            title = "Move 12",
            rule = "Move one pawn 12 spaces forward."
        ) to 4,
        Card(
            label = "SORRY!",
            title = "Sorry!",
            rule = "Take a pawn from Start, place it on any opponent's pawn, and send that pawn back to Start.",
            isSorry = true
        ) to 4,
    )

    /** Returns a fresh, shuffled 45-card deck. */
    fun newShuffled(): List<Card> {
        val deck = mutableListOf<Card>()
        templates.forEach { (card, count) -> repeat(count) { deck.add(card) } }
        check(deck.size == 45) { "Deck must be 45 cards, was ${deck.size}" }
        return deck.shuffled()
    }
}
