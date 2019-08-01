package io.github.yunato.myankicard.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.AnkiCard
import io.github.yunato.myankicard.other.aws.DailyCardsTask

class InitialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        val getTask = DailyCardsTask()
        getTask.setOnFinishListener(object: DailyCardsTask.OnFinishListener {
            override fun onFinish(cardList: List<AnkiCard>) {
                startActivity(MainActivity.intent(this@InitialActivity))
            }
        })
        getTask.execute()
    }
}
