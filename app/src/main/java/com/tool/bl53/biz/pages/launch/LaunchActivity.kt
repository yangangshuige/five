package com.tool.bl53.biz.pages.launch

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.tool.bl53.R
import com.tool.bl53.api.ApiConstants
import com.tool.bl53.utils.LogUtils

class LaunchActivity : AppCompatActivity() {
    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        val hostFragment = NavHostFragment.create(R.navigation.launch_nav_graph)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, hostFragment)
            .commit()
        val lifecycleEventObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_CREATE) {
                    onCreateHostFragment(hostFragment)
                    hostFragment.lifecycle.removeObserver(this)
                }
            }

        }
        hostFragment.lifecycle.addObserver(lifecycleEventObserver)

    }

    private fun onCreateHostFragment(hostFragment: NavHostFragment) {
        navController = hostFragment.findNavController()
        if (isLogin()) {
            navController?.navigate(R.id.main_activity)
            finishAfterTransition()
        } else {
            navController?.navigate(R.id.loginFragment)
        }
    }

    private fun isLogin(): Boolean {
        val sharedPreferences = getSharedPreferences(ApiConstants.PREFERENCES_NAME, 0)
        val time = sharedPreferences.getLong(ApiConstants.EXPORT_TIME, 0)
        return System.currentTimeMillis() < time
    }
}