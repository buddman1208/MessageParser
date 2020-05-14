package com.example.messageparser

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.recyclical.ViewHolder
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.messageparser.db.Failure
import com.example.messageparser.db.FailureDB
import com.example.messageparser.db.Set
import kotlinx.android.synthetic.main.activity_failure.*
import org.jetbrains.anko.doAsyncResult
import java.net.URLDecoder
import java.text.SimpleDateFormat

class FailureActivity : AppCompatActivity() {
    val failureList: DataSource<Failure> = dataSourceTypedOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failure)

        rvList.setup {
            withDataSource(failureList)
            withItem<Failure, FailureViewHolder>(R.layout.layout_set_list) {
                onBind(::FailureViewHolder) { index, item ->
                    title.text = item.message.decodeUtf8()
                    date.text = "${item.sender} -> ${item.received}, ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.createDate)}"
                }
                onClick { position ->
                    openDialog(position)
                }
            }
        }
        rvList.apply {
            layoutManager = LinearLayoutManager(this@FailureActivity)
            addItemDecoration(DividerItemDecoration(this@FailureActivity, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        val list = doAsyncResult {
            FailureDB.getInstance(applicationContext)?.failureDao()?.loadAllFailures()
        }.get()
        list?.run {
            failureList.clear()
            failureList.addAll(this)
            updateStatus(failureList.size())
        }
    }

    private fun removeFailure(failure : Failure) {
        val list = doAsyncResult {
            FailureDB.getInstance(applicationContext)?.failureDao()?.deleteFailures(failure)
        }.get()
        updateList()
    }

    private fun updateStatus(itemCount : Int) {
        tvStatus.text = "현재 등록된 실패 내역 $itemCount 건"
    }

    fun openDialog(position: Int) {
        val item = failureList[position]

        MaterialDialog(this@FailureActivity).show {
            title(text = "실패 내역")
            message(text = "키 : ${item.key}\n정보 : ${item.sender} -> ${item.received}, ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.createDate)}\n메시지 : ${item.message.decodeUtf8()}")
            positiveButton(R.string.retry) {
                SMSEventHandler.sendToServer(Set(
                    0L, "", item.sender, item.url, item.key
                ), item.sender, item.message.decodeUtf8())
            }
            negativeButton(R.string.delete) {
                removeFailure(item)
            }
        }

    }


}
class FailureViewHolder(itemView: View) : ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tvTitle)
    val date: TextView = itemView.findViewById(R.id.tvNumber)
}

fun String.decodeUtf8(): String = URLDecoder.decode(this, "UTF-8")