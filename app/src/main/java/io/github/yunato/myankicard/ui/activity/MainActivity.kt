package io.github.yunato.myankicard.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.ui.fragment.MainFragment

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

        if (getPrimaryKeyForInterruption() != -1L){
            showDialog()
        }
    }

    private fun getPrimaryKeyForInterruption(): Long {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getLong(App.PRAM_PRIMARY_KEY, -1L)
    }

    private fun removePrimaryKeyForInterruption() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().remove(App.PRAM_PRIMARY_KEY).apply()
    }

    private fun showDialog() {
        AlertDialog.Builder(this).apply {
            setMessage(getText(R.string.dialog_interruption_message))
            setPositiveButton(getText(R.string.dialog_interruption_positive_text)) { _, _ ->
                removePrimaryKeyForInterruption()
            }
            setNegativeButton(getText(R.string.dialog_interruption_negative_text)) { _, _ ->
                removePrimaryKeyForInterruption()
            }
        }.show()
    }

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
    }
}
