package io.github.yunato.myankicard.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.ui.fragment.EndFragment
import io.github.yunato.myankicard.ui.fragment.QAFragment
import io.github.yunato.myankicard.ui.fragment.StartFragment

class QAActivity : AppCompatActivity() {

    private var qaFragment: QAFragment = QAFragment.newInstance()

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

    init {
        qaFragment.setOnReadyListener(qaReadyListener)
        qaFragment.setOnFinishListener(qaListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null){
            qaFragment.fetchQACardFromDB()
        }
    }

    companion object{
        fun intent(context: Context): Intent = Intent(context, QAActivity::class.java)
    }
}
