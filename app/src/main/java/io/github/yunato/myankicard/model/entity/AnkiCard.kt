package io.github.yunato.myankicard.model.entity

data class AnkiCard(val timestamp: Int,
                    val question: String,
                    val answer: String,
                    val next_date: Int,
                    val consecutive: Int,
                    var is_correct: Boolean)
