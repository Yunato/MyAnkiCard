package io.github.yunato.myankicard.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.ui.fragment.MainFragment
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null){
            fragment = MainFragment.newInstance()
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        }
    }

    override fun onResume() {
        super.onResume()

        val now = Calendar.getInstance()
        now.set(Calendar.HOUR, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        val stamp = now.time.time / 1000
        if(getStamp() != stamp){
            // TODO DB check
            setStamp(stamp)
        }
    }

    private fun setStamp(stamp: Long) {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putLong(STATE_STAMP, stamp).apply()
    }

    private fun getStamp(): Long {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getLong(STATE_STAMP, PREFERENCE_INIT)
    }

    companion object {
        @JvmStatic private val STATE_STAMP: String = "io.github.yunato.myankicard.ui.activity.STATE_STAMP"
        @JvmStatic private val PREFERENCE_INIT: Long = 0

        fun intent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
    }
}
