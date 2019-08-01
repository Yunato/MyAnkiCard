package io.github.yunato.myankicard.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.ui.activity.QAActivity
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daily_learn_button.setOnClickListener {
            startActivity(QAActivity.intent(activity as Context, QAActivity.STATE_LEARN))
        }

        daily_test_button.setOnClickListener {
            startActivity(QAActivity.intent(activity as Context, QAActivity.STATE_TEST_DAILY))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
