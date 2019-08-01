package io.github.yunato.myankicard.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.ui.fragment.*

class QAActivity : AppCompatActivity() {

    private var state: Int = STATE_LEARN
    private lateinit var qaFragment: QAFragment

    private val startListener: StartFragment.OnFinishListener = object: StartFragment.OnFinishListener {
        override fun onFinish() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, qaFragment).commit()
        }
    }

    private val qaListener: QAFragment.OnFinishListener = object: QAFragment.OnFinishListener {
        override fun onFinish(quest_num: Int, correct_num: Int, mistake_num: Int) {
            val fragment = EndFragment.newInstance(quest_num, correct_num, mistake_num)
            fragment.setOnFinishListener(endListener)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()
        }
    }

    private val endListener: EndFragment.OnFinishListener = object: EndFragment.OnFinishListener {
        override fun onFinish() {
            finish()
        }
    }

    private val qaReadyListener: QAFragment.OnReadyListener = object: QAFragment.OnReadyListener {
        override fun onReady() {
            val fragment = StartFragment.newInstance()
            fragment.setOnFinishListener(startListener)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)

        state = intent.getIntExtra(STATE_EXTRA, STATE_LEARN)
        qaFragment = when (state) {
            STATE_LEARN -> LearnQAFragment.newInstance()
            STATE_TEST_DAILY -> TestQAFragment.newInstance()
            else -> throw IllegalStateException("State is not correct")
        }
        qaFragment.setOnReadyListener(qaReadyListener)
        qaFragment.setOnFinishListener(qaListener)

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null){
            qaFragment.fetchQACardFromDB()
        }
    }

    companion object{
        @JvmStatic private val STATE_EXTRA = "io.github.yunato.myankicard.ui.activity.STATE_EXTRA"
        @JvmStatic val STATE_LEARN = 0
        @JvmStatic val STATE_TEST_DAILY = 1
        @JvmStatic val STATE_TEST_RANDOM = 2

        fun intent(context: Context, state: Int): Intent =
            Intent(context, QAActivity::class.java).putExtra(STATE_EXTRA, state)
    }
}
