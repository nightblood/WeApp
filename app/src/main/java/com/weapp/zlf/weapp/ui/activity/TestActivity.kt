package com.weapp.zlf.weapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

import com.weapp.zlf.weapp.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import java.net.URL

/**
 * Created by zhuliangfei on 2018/2/12.
 */

class TestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val tvName: TextView? = find(R.id.tvName)
        tvName?.text = "sdf"

        doAsync {
            val response = URL("https://www.baidu.com").readText()
            uiThread {
                tvName?.text = response
            }
        }
        alert("Order", "Do you want to order this item?") {
            positiveButton("Yes") { Toast.makeText(this@TestActivity, "haha", Toast.LENGTH_LONG) }
            negativeButton("No") { }
        }.show()
    }

    companion object {

        fun launch(context: Context) {
            val intent = Intent(context, TestActivity::class.java)
            context.startActivity(intent)
        }
    }
}
