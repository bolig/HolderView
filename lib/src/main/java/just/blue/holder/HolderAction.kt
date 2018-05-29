package just.blue.holder

/**
 * Created by JustBlue on 2018/5/29.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
interface HolderAction {
    fun showEmpty() // 显示空数据界面
    fun showError() // 显示业务异常界面
    fun showLoading() // 显示加载业务数据界面
    fun showNetMiss() // 显示网路异常界面
    fun showContent() // 显示业务数据

    /**
     * 显示指定状态码的状态界面, 支持自定义状态码
     * 注: 状态码应该具有一定的全局性(即一个状态码
     * 绑定一种状态, 不应该重复指定), 因为[HolderView]
     * 中将[state]作为唯一标示, 重复的状态码, 可能会使
     * 界面展示异常, 请避免使用以下状态码:
     * [HolderView.state_normal]
     * [HolderView.state_empty]
     * [HolderView.state_error]
     * [HolderView.state_loading]
     * [HolderView.state_netMiss]
     * [HolderView.state_content]
     *
     * @param
     */
    fun showByState(state: Int) // 通过状态显示控件
}