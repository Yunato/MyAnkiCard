package io.github.yunato.myankicard.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import kotlinx.android.synthetic.main.view_holder_qa.view.*
import kotlin.random.Random

class QAViewPagerAdapter(private val fragment: Fragment): PagerAdapter() {

    private var qaList = listOf<Int>().toMutableList()
    private val elementNum = 5

    fun initializeQA() {
        qaList.clear()

        for (i in 0 until elementNum) {
            qaList.add(Random.nextInt(10))
        }
    }

    fun forwardQA() {
        for (i in 0 until 3) {
            qaList.removeAt(0)
        }

        for (i in 0 until 3) {
            qaList.add(Random.nextInt(10))
        }
    }

    fun rewindQA() {
        for (i in 0 until 3) {
            qaList.removeAt(5 - i - 1)
        }

        for (i in 0 until 3) {
            // TODO Add value to qaList
            // qaList.add(0, )
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return elementNum
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(fragment.context).inflate(R.layout.view_holder_qa, container, false)
        view.textView.text = qaList[position].toString()

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
