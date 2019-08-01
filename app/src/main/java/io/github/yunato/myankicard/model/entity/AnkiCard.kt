package io.github.yunato.myankicard.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class AnkiCard(@PrimaryKey
                    val timestamp: Long,
                    @SerializedName("category1")
                    val mainCategory: String,
                    @SerializedName("category2")
                    val subCategory: String,
                    val question: String,
                    @SerializedName("image_id")
                    val imageId: Long,
                    val answer: String,
                    @SerializedName("date_available_for_questions")
                    val nextDate: Long,
                    @SerializedName("number_of_consecutive_correct_answers")
                    val consecutive: Long,
                    var isCorrect: Boolean)
