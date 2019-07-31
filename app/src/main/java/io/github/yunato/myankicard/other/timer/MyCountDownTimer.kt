package io.github.yunato.myankicard.other.timer

import android.os.CountDownTimer

class MyCountDownTimer(millisInFuture: Long,
                       countDownInterval: Long = 100) : CountDownTimer(millisInFuture, countDownInterval) {
    private var mListener: OnProgressListener? = null

    fun setOnProgressListener(listener: OnProgressListener){
        mListener = listener
    }

    override fun onTick(millisUntilFinished: Long) {}

    override fun onFinish(){
        mListener?.onProgress(0)
    }

    interface OnProgressListener {
        fun onProgress(time: Long)
    }
}
