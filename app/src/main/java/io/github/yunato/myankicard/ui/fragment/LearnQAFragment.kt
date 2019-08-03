package io.github.yunato.myankicard.ui.fragment

import android.content.SharedPreferences
import android.preference.PreferenceManager
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*

class LearnQAFragment : QAFragment() {

    override val adapter: QAViewPagerAdapter = QAViewPagerAdapter(this, true)

    override val progressListenr: MyCountDownTimer.OnProgressListener = object: MyCountDownTimer.OnProgressListener {
        override fun onProgress(time: Long) {
            if(time == 0L){
                if (qaIndex >= mCardList.size - 1) {
                    isFinished = true
                    finishListener?.onFinish(0, 0, 0)
                } else {
                    ++qaIndex
                    viewPager.setCurrentItem(++pageIndex, true)
                    startAutoSwipe()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        removePrimaryKeyForInterruption()
    }

    override fun onPause() {
        super.onPause()
        if (!isFinished) setPrimaryKeyForInterruption()
    }

    private fun setPrimaryKeyForInterruption() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        sp.edit().putLong(App.PRAM_PRIMARY_KEY, mCardList[qaIndex].timestamp).apply()
    }

    private fun removePrimaryKeyForInterruption() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        sp.edit().remove(App.PRAM_PRIMARY_KEY).apply()
    }

    override fun startAutoSwipe(){
        val textSize = mCardList[qaIndex].question.length + mCardList[qaIndex].answer.length
        val millisInFuture = ((textSize / 8).toDouble() * 1000L).toLong()
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(progressListenr)
        timer?.start()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LearnQAFragment()
    }
}
