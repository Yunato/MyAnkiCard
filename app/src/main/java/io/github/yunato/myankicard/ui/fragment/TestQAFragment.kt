package io.github.yunato.myankicard.ui.fragment


import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*
import kotlin.concurrent.thread

class TestQAFragment : QAFragment() {

    private var mistakeNum: Int = 0

    override val progressListenr: MyCountDownTimer.OnProgressListener = object: MyCountDownTimer.OnProgressListener{
        override fun onProgress(time: Long) {
            if(time == 0L){
                val isCorrect = (viewPager.adapter as QAViewPagerAdapter).getAnsCorrect(pageIndex)
                val dao = App.cardDataBase.ankiCardDao()
                val index = qaIndex
                thread {
                    val card = dao.findOneCard(mCardList[index].timestamp)
                    card.isCorrect = isCorrect
                    App.cardDataBase.ankiCardDao().updateCard(card)
                }
                if (!isCorrect) ++mistakeNum
                if (qaIndex == mCardList.size - 1) {
                    finishListener?.onFinish(mCardList.size, mCardList.size - mistakeNum, mistakeNum)
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
