package io.github.yunato.myankicard.ui.fragment

import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.activity.QAActivity
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*

class LearnQAFragment : QAFragment() {

    override val adapter: QAViewPagerAdapter = QAViewPagerAdapter(this, true)

    override val progressListener: MyCountDownTimer.OnProgressListener = object: MyCountDownTimer.OnProgressListener {
        override fun onProgress(time: Long) {
            if(time == 0L){
                if (qaIndex >= mCardList.size - 1) {
                    isEnded = true
                    (activity as QAActivity).switchFragment(mCardList.size, 0, 0)
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
        App.preference.removePrimaryKey()
    }

    override fun onPause() {
        super.onPause()
        if (!isEnded) App.preference.primaryKey = mCardList[qaIndex].timestamp
    }

    override fun startAutoSwipe(){
        val textSize = mCardList[qaIndex].question.length + mCardList[qaIndex].answer.length
        val millisInFuture = ((textSize / 8).toDouble() * 1000L).toLong()
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(progressListener)
        timer?.start()
    }

    companion object {
        fun newInstance() = LearnQAFragment()
    }
}
