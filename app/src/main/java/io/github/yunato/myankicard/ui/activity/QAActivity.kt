package io.github.yunato.myankicard.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.ui.fragment.QAFragment

class QAActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null){
            fragment = QAFragment.newInstance()
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        }
    }
}
