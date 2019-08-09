package io.github.yunato.myankicard.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.ui.activity.QAActivity
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daily_learn_button.setOnClickListener {
            startActivity(QAActivity.intent(activity as Context, QAActivity.MODE_LEARN_DAILY))
        }

        daily_test_button.setOnClickListener {
            startActivity(QAActivity.intent(activity as Context, QAActivity.MODE_TEST_DAILY))
        }

        if (App.preference.primaryKey != -1L){
            showDialog()
        }
    }

    private fun showDialog() {
        AlertDialog.Builder(activity).apply {
            setMessage(getText(R.string.dialog_interruption_message))
            setPositiveButton(getText(R.string.dialog_interruption_positive_text)) { _, _ ->
                startActivity(QAActivity.intent(activity as Context, QAActivity.MODE_LEARN_DAILY))
            }
            setNegativeButton(getText(R.string.dialog_interruption_negative_text)) { _, _ ->
                App.preference.removePrimaryKey()
            }
        }.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
