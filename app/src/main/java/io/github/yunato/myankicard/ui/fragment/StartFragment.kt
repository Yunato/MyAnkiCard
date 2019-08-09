package io.github.yunato.myankicard.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.activity.QAActivity
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {

    private var timer: MyCountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCountDown()
    }

    private fun startCountDown() {
        val millisInFuture = 4 * 1000L
        val interval = 50L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(object: MyCountDownTimer.OnProgressListener {
            override fun onProgress(time: Long) {
                when {
                    time == 0L -> (this@StartFragment.activity as QAActivity).switchFragment()
                    time < 1000L -> timerText.setText(R.string.start_text)
                    else -> timerText.text = ((time / 1000).toInt()).toString()
                }
            }
        })
        timer?.start()
    }

    companion object {
        fun newInstance() = StartFragment()
    }
}
