package io.github.yunato.myankicard.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.github.yunato.myankicard.R
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.settings) {
            startActivity(SettingsActivity.intent(this))
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
    }
}
