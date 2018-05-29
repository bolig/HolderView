package just.blue.holder.smaple

import android.os.Bundle
import android.os.Handler
import just.blue.holder.smaple.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        btn_refresh.setOnClickListener {
            loadData()
        }
    }

//    override fun loadData() {
//        showLoading()
//
//        Handler().postDelayed({
//            when (rn.nextInt(5)) {
//                0 -> showError()
//                1 -> showEmpty()
//                3 -> showNetMiss()
//                else -> showContent()
//            }
//        }, 2000)
//    }
}
