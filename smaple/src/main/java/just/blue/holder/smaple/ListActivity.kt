package just.blue.holder.smaple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import just.blue.holder.smaple.adapter.TestAdapter
import just.blue.holder.smaple.base.BaseActivity
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : BaseActivity() {

    companion object {
        fun start(act: Activity) {
            val intent = Intent(act, ListActivity::class.java)

            act.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_list

    override fun initView(savedInstanceState: Bundle?) {
        rv_content.layoutManager = LinearLayoutManager(this)
        rv_content.adapter = TestAdapter()
    }
}
