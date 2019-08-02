package io.github.yunato.myankicard.other.task

import android.os.AsyncTask
import io.github.yunato.myankicard.model.dao.AnkiCardDao
import io.github.yunato.myankicard.model.entity.AnkiCard
import io.github.yunato.myankicard.other.application.App

class FetchFromDBTask : AsyncTask<Unit, Unit, Unit>() {

    private var mListener: OnFinishListener? = null

    private lateinit var dao: AnkiCardDao

    private lateinit var cardList: List<AnkiCard>

    override fun onPreExecute() {
        dao = App.cardDataBase.ankiCardDao()
    }

    override fun doInBackground(vararg params: Unit?) {
        cardList = dao.findAllDaily()
    }

    override fun onPostExecute(result: Unit?) {
        mListener?.onFinish(cardList)
    }

    fun setOnFinishListener(listener: OnFinishListener){
        mListener = listener
    }

    interface OnFinishListener {
        fun onFinish(cardList :List<AnkiCard>)
    }
}
