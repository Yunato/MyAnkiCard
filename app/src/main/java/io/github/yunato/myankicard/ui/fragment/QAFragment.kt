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
import kotlinx.android.synthetic.main.fragment_qa.view.*
import kotlin.concurrent.thread

abstract class QAFragment : Fragment() {

    lateinit var mCardList: List<QACard>

    val adapter: QAViewPagerAdapter = QAViewPagerAdapter(this)

    protected var finishListener: OnFinishListener? = null

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

    abstract val progressListenr: MyCountDownTimer.OnProgressListener

    private var timer: MyCountDownTimer? = null
    protected var pageIndex: Int = 0
    protected var qaIndex: Int = 0
    protected var mistake_num: Int = 0

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

    protected fun startAutoSwipe(){
        // TODO Text Length
        val millisInFuture = 3 * 1000L
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(progressListenr)
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
}
