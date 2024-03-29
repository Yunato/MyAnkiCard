package io.github.yunato.myankicard.model.entity

data class QACard(val timestamp: Long,
                  val question: String,
                  val answer: String,
                  val next_date: Long,
                  val consecutive: Long,
                  var is_correct: Boolean) {

    constructor(card: AnkiCard): this(card.timestamp, card.question, card.answer, card.nextDate, card.consecutive, true)
}
