package io.github.yunato.myankicard.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.QACard
import io.github.yunato.myankicard.other.application.App
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*
import kotlinx.android.synthetic.main.fragment_qa.view.*
import kotlin.concurrent.thread

class QAFragment : Fragment() {

    lateinit var mCardList: List<QACard>

    val adapter: QAViewPagerAdapter = QAViewPagerAdapter(this)

    private var finishListener: OnFinishListener? = null

    private var readyListener: OnReadyListener? = null

    private val viewPagerListener: ViewPager.OnPageChangeListener = object: ViewPager.OnPageChangeListener {

        private var jumpPosition = -1

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            if (position == 0 && qaIndex != 0) {
                jumpPosition = 3
                adapter.rewindQA()
            } else if (position == 4) {
                jumpPosition = 1
                adapter.forwardQA()
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE && jumpPosition >= 0) {
                view?.let {
                    it.viewPager.setCurrentItem(jumpPosition, false)
                    pageIndex = jumpPosition
                    jumpPosition = -1
                }
            }
        }
    }

    private var timer: MyCountDownTimer? = null
    private var pageIndex: Int = 0
    private var qaIndex: Int = 0
    private var mistake_num: Int = 0

    fun fetchQACardFromDB() {
        val dao = App.cardDataBase.ankiCardDao()
        thread {
            val cards = dao.findAll()
            val qaCards = mutableListOf<QACard>()
            for (card in cards) {
                qaCards.add(QACard(
                    card.timestamp,
                    card.question,
                    card.answer,
                    card.nextDate,
                    card.consecutive,
                    card.isCorrect
                ))
            }
            mCardList = qaCards
            readyListener?.onReady()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.initializeQA(mCardList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_qa, container, false)

        view.viewPager.addOnPageChangeListener(viewPagerListener)
        view.viewPager.adapter = adapter
        view.viewPager.setCurrentItem(pageIndex, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAutoSwipe()
    }

    private fun startAutoSwipe(){
        // TODO Text Length
        val millisInFuture = 3 * 1000L
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(object: MyCountDownTimer.OnProgressListener{
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
        })
        timer?.start()
    }

    fun setOnReadyListener(listener: OnReadyListener) {
        readyListener = listener
    }

    fun setOnFinishListener(listener: OnFinishListener) {
        finishListener = listener
    }

    interface OnReadyListener {
        fun onReady()
    }

    interface OnFinishListener {
        fun onFinish(quest_num: Int, correct_num: Int, mistake_num: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance() = QAFragment()
    }
}
