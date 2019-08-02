package io.github.yunato.myankicard.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.ui.fragment.*

class QAActivity : AppCompatActivity() {

    private var mode: Int = MODE_LEARN
    private var phase: Int = PHASE_START
    private lateinit var qaFragment: QAFragment

    private val startListener: StartFragment.OnFinishListener = object: StartFragment.OnFinishListener {
        override fun onFinish() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, qaFragment).commit()
            phase = PHASE_QA
        }
    }

    private val qaListener: QAFragment.OnFinishListener = object: QAFragment.OnFinishListener {
        override fun onFinish(quest_num: Int, correct_num: Int, mistake_num: Int) {
            val fragment = EndFragment.newInstance(quest_num, correct_num, mistake_num)
            fragment.setOnFinishListener(endListener)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()
            phase = PHASE_END
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

        override fun onFault() {
            Toast.makeText(this@QAActivity, getText(R.string.toast_message), Toast.LENGTH_SHORT).show()
            this@QAActivity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)

        mode = intent.getIntExtra(MODE_EXTRA, MODE_LEARN)
        qaFragment = when (mode) {
            MODE_LEARN -> LearnQAFragment.newInstance()
            MODE_TEST_DAILY -> TestQAFragment.newInstance()
            else -> throw IllegalStateException("State is not correct")
        }
        qaFragment.setOnReadyListener(qaReadyListener)
        qaFragment.setOnFinishListener(qaListener)

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null){
            val stampForFirstList = getPrimaryKeyForInterruption()
            qaFragment.fetchQACardFromDB(stampForFirstList)
        }
    }

    private fun getPrimaryKeyForInterruption(): Long {
        if (MODE_LEARN != mode) return -1L
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getLong(App.PRAM_PRIMARY_KEY, -1L)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (PHASE_QA == phase) {
                qaFragment.onPressBackKey()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    companion object{
        @JvmStatic private val MODE_EXTRA = "io.github.yunato.myankicard.ui.activity.STATE_MODE"
        @JvmStatic val MODE_LEARN = 0
        @JvmStatic val MODE_TEST_DAILY = 1
        @JvmStatic val MODE_TEST_RANDOM = 2

        @JvmStatic private val PHASE_START = 0
        @JvmStatic private val PHASE_QA = 1
        @JvmStatic private val PHASE_END = 2

        fun intent(context: Context, state: Int): Intent =
            Intent(context, QAActivity::class.java).putExtra(MODE_EXTRA, state)
    }
}
