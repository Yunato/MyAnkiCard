package io.github.yunato.myankicard.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import io.github.yunato.myankicard.model.entity.AnkiCard
import kotlinx.android.synthetic.main.view_holder_qa.view.*

class QAViewPagerAdapter(private val fragment: Fragment): PagerAdapter() {

    private var qaList = listOf<AnkiCard>().toMutableList()
    private var elementNum = 0

    fun initializeQA(cardList: List<AnkiCard>) {
        qaList.clear()
        qaList = cardList.toMutableList()
        elementNum = cardList.size
    }

    fun forwardQA() {
        for (i in 0 until 3) {
            qaList.removeAt(0)
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

    fun getAnsCorrect(index: Int): Boolean = qaList[index].is_correct

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return elementNum
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(fragment.context).inflate(R.layout.view_holder_qa, container, false)
        val ankiCard = if(qaList.size <= position){
            AnkiCard(0, "", "", 0, 0, true)
        }else {
            qaList[position]
        }
        view.question_text.text = ankiCard.question
        view.answer_text.text = ankiCard.answer
        view.tag = ankiCard
        view.setOnClickListener{
            qaList[position].is_correct = false
        }

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
