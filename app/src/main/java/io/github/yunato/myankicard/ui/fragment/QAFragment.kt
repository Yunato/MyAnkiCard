package io.github.yunato.myankicard.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.other.timer.MyCountDownTimer
import io.github.yunato.myankicard.ui.adapter.QAViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_qa.*
import kotlinx.android.synthetic.main.fragment_qa.view.*

class QAFragment : Fragment() {

    val adapter: QAViewPagerAdapter = QAViewPagerAdapter(this)

    private var listener: OnFragmentInteractionListener? = null

    private val viewPagerListener: ViewPager.OnPageChangeListener = object: ViewPager.OnPageChangeListener {

        private var jumpPosition = -1

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            if (position == 0) {
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
                    jumpPosition = -1
                }
            }
        }
    }

    private var timer: MyCountDownTimer? = null
    private var pageIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.initializeQA()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_qa, container, false)

        view.viewPager.addOnPageChangeListener(viewPagerListener)
        view.viewPager.adapter = adapter
        view.viewPager.setCurrentItem(1, false)
        pageIndex = 1

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAutoSwipe()
    }

    private fun startAutoSwipe(){
        val millisInFuture = 5 * 1000L
        val interval = 200L
        timer?.cancel()
        timer = MyCountDownTimer(millisInFuture, interval)
        timer?.setOnProgressListener(object: MyCountDownTimer.OnProgressListener{
            override fun onProgress(time: Long) {
                if(time == 0L){
                    if (pageIndex == 100) {
                    } else {
                        ++pageIndex
                        startAutoSwipe()
                    }
                }
            }
        })
        viewPager.currentItem = pageIndex
        timer?.start()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = QAFragment()
    }
}
