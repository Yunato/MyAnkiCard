package io.github.yunato.myankicard.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.AnkiCard
import kotlinx.android.synthetic.main.view_holder_qa.view.*

class QAViewPagerAdapter(val fragment: Fragment): PagerAdapter() {

    private var qaList = listOf<AnkiCard>().toMutableList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 100
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(fragment.context).inflate(R.layout.view_holder_qa, container, false)
        view.textView.text = position.toString()

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
