package com.example.messageparser

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.ViewHolder
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    val setList: DataSource<Set> = dataSourceTypedOf()
    var isLoaded : Boolean = false

    override fun onResume() {
        super.onResume()
        if(isLoaded) updateList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        updateStatus(0)
        rvList.setup {
            withDataSource(setList)
            withItem<Set, ListViewHolder>(R.layout.layout_set_list) {
                onBind(::ListViewHolder) { index, item ->
                    title.text = item.title
                    number.text = item.from
                }
                onClick {
                    startActivity(intentFor<AddEditActivity>("target" to item))
                }
            }
        }
        rvList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }
        btnAdd.setOnClickListener {
            startActivity<AddEditActivity>()
        }
        updateList()
        isLoaded = true
    }

    private fun checkPermission() {
        TedPermission.with(this@MainActivity)
            .setPermissionListener(object : PermissionListener{
                override fun onPermissionGranted() {

                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                    finish()
                }
            })
            .setRationaleMessage("앱을 사용하기 위해 권한이 필요합니다.")
            .setDeniedMessage("권한이 거부되어 앱을 사용할 수 없습니다. [설정] > [권한] 에서 권한을 허용해주세요.")
            .setPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_PHONE_STATE)
            .check()
    }

    private fun updateList() {
        val list = doAsyncResult {
            SetDB.getInstance(applicationContext)?.setDao()?.loadAllSets()
        }.get()
        list?.run {
            setList.clear()
            setList.addAll(this)
            updateStatus(setList.size())
        }
    }

    private fun updateStatus(itemCount : Int) {
        tvStatus.text = "현재 등록된 설정값 $itemCount 건"
    }
}

class ListViewHolder(itemView: View) : ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tvTitle)
    val number: TextView = itemView.findViewById(R.id.tvNumber)
}