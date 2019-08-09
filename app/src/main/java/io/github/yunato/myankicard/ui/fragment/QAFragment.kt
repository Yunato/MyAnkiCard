package io.github.yunato.myankicard.ui.fragment

import android.app.AlertDialog
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

abstract class QAFragment : Fragment() {

    lateinit var mCardList: List<QACard>

    abstract val adapter: QAViewPagerAdapter

    private var hasPaused = false

    protected var isFinished = false

    protected var finishListener: OnFinishListener? = null

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

    protected var timer: MyCountDownTimer? = null
    protected var pageIndex: Int = 0
    protected var qaIndex: Int = 0

    fun fetchQACardFromDB(stampForFirst: Long): Boolean {
        val dao = App.cardDataBase.ankiCardDao()
        val cardList = dao.findAllDaily()
        val qaCards = mutableListOf<QACard>()
        for (card in cardList) {
            if (card.timestamp == stampForFirst) qaCards.clear()
            qaCards.add(QACard(card))
        }
        mCardList = qaCards
        return mCardList.isNotEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.initializeQA(mCardList)
    }

    override fun onResume() {
        super.onResume()

        if (hasPaused) {
            showDialog()
        }
    }

    override fun onPause() {
        super.onPause()

        hasPaused = true
        timer?.cancel()
    }

    fun onPressBackKey() {
        timer?.cancel()
        showDialog()
    }

    private fun showDialog() {
        AlertDialog.Builder(activity).apply {
            setMessage(getText(R.string.dialog_message))
            setPositiveButton(getText(R.string.dialog_positive_text)) { _, _ ->
                startAutoSwipe()
            }
            setNegativeButton(getText(R.string.dialog_negative_text)) { _, _ ->
                isFinished = true
                activity?.finish()
            }
        }.show()
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

    abstract fun startAutoSwipe()

    fun setOnFinishListener(listener: OnFinishListener) {
        finishListener = listener
    }

    interface OnFinishListener {
        fun onFinish(quest_num: Int, correct_num: Int, mistake_num: Int)
    }
}
