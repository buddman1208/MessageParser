package com.example.messageparser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.messageparser.db.Set
import com.example.messageparser.db.SetDB
import kotlinx.android.synthetic.main.activity_add_edit.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class AddEditActivity : AppCompatActivity() {

    val updateTarget: Set? by lazy {
        intent.getSerializableExtra("target") as Set?
    }

    var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        updateTarget?.run {
            etTitle.setText(title)
            etFrom.setText(from)
            etSendUrl.setText(sendUrl)
            etKey.setText(key)
            btnConfirm.text = "업데이트"
            btnDelete.visibility = View.VISIBLE
            isUpdate = true
        }
        supportActionBar?.title = if (isUpdate) "수정" else "추가"
        btnConfirm.setOnClickListener {
            updateOrAdd(
                title = etTitle.text.toString().trim(),
                from = etFrom.text.toString().trim(),
                sendUrl = etSendUrl.text.toString().trim(),
                key = etKey.text.toString().trim()
            )
        }
        btnDelete.setOnClickListener { remove() }
    }

    private fun remove() {
        if (updateTarget == null) return
        else {
            doAsync {
                val dao = SetDB.getInstance(applicationContext)?.setDao()
                dao?.deleteSets(updateTarget!!)
            }
            toast("삭제되었습니다.")
            finish()
        }
    }

    private fun updateOrAdd(
        title: String,
        from: String,
        sendUrl: String,
        key: String
    ) {
        if (checkNotBlank(title, from, sendUrl, key)) {
            if(validateUrl(sendUrl)) {
                val target: Set = updateTarget?.apply {
                    this.title = title
                    this.from = from
                    this.sendUrl = sendUrl
                    this.key = key
                } ?: Set(
                    title = title, from = from, sendUrl = sendUrl, key = key
                )
                processToDb(target, isUpdate)
            } else toast("올바른 URL 주소를 입력해주세요.")
        } else toast("빈칸 없이 입력해주세요.")
    }

    private fun processToDb(set: Set, isUpdate: Boolean) {
        doAsync {
            val dao = SetDB.getInstance(applicationContext)?.setDao()
            if (isUpdate) {
                dao?.updateSets(set)
            } else {
                dao?.insertSets(set)
            }
        }
        toast("완료되었습니다.")
        finish()
    }

    private fun checkNotBlank(vararg str: String): Boolean =
        str.toList().all { it.trim().isNotBlank() }

    private fun validateUrl(url: String): Boolean =
        url.startsWith("http://") || url.startsWith("https://")
}
