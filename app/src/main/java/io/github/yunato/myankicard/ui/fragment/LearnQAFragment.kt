package io.github.yunato.myankicard.ui.fragment

import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import kotlinx.android.synthetic.main.fragment_qa.*

class LearnQAFragment : QAFragment() {

    override val progressListenr: MyCountDownTimer.OnProgressListener = object: MyCountDownTimer.OnProgressListener {
        override fun onProgress(time: Long) {
            if(time == 0L){
                if (qaIndex == mCardList.size - 1) {
                    finishListener?.onFinish(0, 0, 0)
                } else {
                    ++qaIndex
                    viewPager.setCurrentItem(++pageIndex, true)
                    startAutoSwipe()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LearnQAFragment()
    }
}
