package io.github.yunato.myankicard.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.yunato.myankicard.R

class TestQAFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test_qa, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() = TestQAFragment()
    }
}
