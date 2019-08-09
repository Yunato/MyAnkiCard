package io.github.yunato.myankicard.model.entity

import com.google.gson.annotations.SerializedName

data class PostResult(val timestamp: Long,
                      @SerializedName("date_available_for_questions")
                      val nextDate: Long,
                      @SerializedName("number_of_consecutive_correct_answers")
                      val consecutive: Long,
                      @SerializedName("state")
                      val state: Int)
