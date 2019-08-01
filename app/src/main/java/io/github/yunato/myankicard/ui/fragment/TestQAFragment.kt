package io.github.yunato.myankicard.ui.fragment


import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*

class TestQAFragment : QAFragment() {

    override val progressListenr: MyCountDownTimer.OnProgressListener = object: MyCountDownTimer.OnProgressListener{
        override fun onProgress(time: Long) {
            if(time == 0L){
                val isCorrect = (viewPager.adapter as QAViewPagerAdapter).getAnsCorrect(pageIndex)
                mCardList[qaIndex].is_correct = isCorrect
                if (!isCorrect) ++mistake_num
                if (qaIndex == mCardList.size - 1) {
                    finishListener?.onFinish(mCardList.size, mCardList.size - mistake_num, mistake_num)
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
        fun newInstance() = TestQAFragment()
    }
}
