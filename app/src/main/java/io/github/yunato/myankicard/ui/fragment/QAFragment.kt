package io.github.yunato.myankicard.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.QACard
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.view.*

abstract class QAFragment : Fragment() {

    abstract val progressListener: MyCountDownTimer.OnProgressListener
    abstract val adapter: QAViewPagerAdapter

    lateinit var mCardList: List<QACard>

    private var hasPaused = false
    protected var isEnded = false

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

    protected var timer: MyCountDownTimer? = null
    protected var pageIndex: Int = 0
    protected var qaIndex: Int = 0

    fun setCardList(qaCards: List<QACard>) {
        mCardList = qaCards
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

    override fun onResume() {
        super.onResume()
        if (hasPaused) showDialog()
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
                isEnded = true
                activity?.finish()
            }
        }.show()
    }

    abstract fun startAutoSwipe()
}
