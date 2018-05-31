package just.blue.holder.smaple.adapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import just.blue.holder.smaple.R

/**
 * Created by JustBlue on 2018/5/31.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
class TestAdapter : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_test, parent, false)
        return object : ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 14
    }
}
