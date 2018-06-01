package just.blue.holder

import just.blue.holder.adapter.BaseAdapter

/**
 * Created by JustBlue on 2018/5/17.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
internal interface HolderInternal : HolderAction {

    /**
     * 获取当前显示状态
     */
    fun getCurrentState(): Int

    /**
     * 添加Adapter
     */
    fun addAdapter(adapter: BaseAdapter<*>)

    /**
     * 批量添加[BaseAdapter]
     */
    fun addAdapter(vararg adapters: BaseAdapter<*>)

    /**
     * 移除adapter
     */
    fun removeAdapter(adapter: BaseAdapter<*>)

    /**
     * 通知View刷新(即调用指定Adapter[Adapter.onViewConvert]),
     * 不会创建新的View
     * 注: 如指定状态View为null, 无操作
     *
     * @param state 不传, 默认刷新当前状态
     */
    fun notifyViewChange(state: Int = getCurrentState())

    /**
     * 通知[HolderView]指定[state]的View需要重新创建
     * 注: 如指定状态View为null, 会创建新的, 但不会显示
     *
     * @param state 不传, 默认刷新当前状态
     */
    fun notifyViewCreate(state: Int = getCurrentState())

    /**
     * ...
     */
    fun onDestroyView()
}