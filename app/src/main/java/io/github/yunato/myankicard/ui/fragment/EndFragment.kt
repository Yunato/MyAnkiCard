package io.github.yunato.myankicard.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.yunato.myankicard.R
import kotlinx.android.synthetic.main.fragment_end.*

class EndFragment : Fragment() {

    private var mListener: OnFinishListener? = null
    private var questNum = 0
    private var correctNum = 0
    private var mistakeNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questNum = it.getInt(ARG_QUEST)
            correctNum = it.getInt(ARG_CORRECT)
            mistakeNum = it.getInt(ARG_MISTAKE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_end, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quest_num_text.text = String.format("%s%s", activity?.getText(R.string.quest_num_text), questNum.toString())
        correct_num_text.text = String.format("%s%s", activity?.getText(R.string.correct_num_text), questNum.toString())
        mistake_num_text.text = String.format("%s%s", activity?.getText(R.string.mistake_num_text), questNum.toString())
        return_button.setOnClickListener{
            mListener?.onFinish()
        }
    }

    fun setOnFinishListener(listener: OnFinishListener){
        mListener = listener
    }

    interface OnFinishListener {
        fun onFinish()
    }

    companion object {
        @JvmStatic private val ARG_QUEST = "io.github.yunato.myankicard.ui.fragment.ARG_QUEST"
        @JvmStatic private val ARG_CORRECT = "io.github.yunato.myankicard.ui.fragment.ARG_CORRECT"
        @JvmStatic private val ARG_MISTAKE = "io.github.yunato.myankicard.ui.fragment.ARG_MISTAKE"

        @JvmStatic
        fun newInstance(quest_num: Int, correct_num: Int, mistake_num: Int): EndFragment{
            return EndFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_QUEST, quest_num)
                    putInt(ARG_CORRECT, correct_num)
                    putInt(ARG_MISTAKE, mistake_num)
                }
            }
        }
    }
}
