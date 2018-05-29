package just.blue.holder

/**
 * 当自定义状态时, 未有与之配对的View()时throw
 */
class UnknownStateException : IllegalAccessException(" undefined state , not find Adapter by state! ")

/**
 * 当指定[HolderView.mViewRecycle]
 */
class ViewRecycleException : IllegalAccessException(" When you reuse you have to be in the state of the View ")

/**
 * 在layout中未找到[HolderView]
 */
class UndefinedLayoutException : IllegalArgumentException(" undefined HolderView in layout")





