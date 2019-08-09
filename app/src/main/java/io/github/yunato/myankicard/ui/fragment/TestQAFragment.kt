package io.github.yunato.myankicard.ui.fragment


import android.os.Bundle
import android.view.View
import android.widget.TextView
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.activity.QAActivity
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class TestQAFragment : QAFragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override val adapter: QAViewPagerAdapter = QAViewPagerAdapter(this, false)

    private var mistakeNum: Int = 0

    private var isThinking: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isThinking = true
    }

    override val progressListener: MyCountDownTimer.OnProgressListener = object: MyCountDownTimer.OnProgressListener{
        override fun onProgress(time: Long) {
            if(time != 0L) return

            if (isThinking) {
                val view = viewPager.findViewWithTag<TextView>(mCardList[qaIndex])
                view.visibility = View.VISIBLE
                isThinking = false
            } else {
                val isCorrect = (viewPager.adapter as QAViewPagerAdapter).getAnsCorrect(pageIndex)
                val dao = App.cardDataBase.ankiCardDao()
                val index = qaIndex

                launch {
                    withContext(Dispatchers.IO) {
                        val card = dao.findOneCard(mCardList[index].timestamp)
                        card.state = if (isCorrect) 1 else 2
                        App.cardDataBase.ankiCardDao().updateCard(card)
                    }
                }
                if (!isCorrect) ++mistakeNum

                if (qaIndex == mCardList.size - 1) {
                    (activity as QAActivity).switchFragment(mCardList.size, mCardList.size - mistakeNum, mistakeNum)
                } else {
                    ++qaIndex
                    viewPager.setCurrentItem(++pageIndex, true)
                    isThinking = true
                }
            }
            startAutoSwipe()
        }
    }

    override fun startAutoSwipe(){
        val millisInFuture = if (isThinking) {
            val textSize = mCardList[qaIndex].question.length + mCardList[qaIndex].answer.length
            ((textSize / 15).toDouble() * 1000L).toLong()
        } else {
            val textSize = mCardList[qaIndex].answer.length
            ((textSize / 10).toDouble() * 1000L).toLong()
        }
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(progressListener)
        timer?.start()
    }

    companion object {
        fun newInstance() = TestQAFragment()
    }
}
