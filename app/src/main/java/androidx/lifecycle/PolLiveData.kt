package androidx.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.PolLiveData.CustomObserver

/**
 * 参考下文实现
 * https://www.jianshu.com/p/e08287ec62cd?utm_campaign=hugo
 */
class PolLiveData<T> : MutableLiveData<T>() {
    @MainThread
    fun observeUnSticky(owner: LifecycleOwner, observer: Observer<in T>) {
        val startVersion = version
        observe(owner, CustomObserver(observer, startVersion))
    }

    internal inner class CustomObserver(
        private val mObserver: Observer<in T>,
        private val mStartVersion: Int
    ) : Observer<T> {
        override fun onChanged(t: T) {
            //此处做拦截操作
            val curV = version
            if (curV > mStartVersion) {
                mObserver.onChanged(t)
            }
        }
    }
}