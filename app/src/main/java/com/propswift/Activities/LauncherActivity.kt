package com.propswift.Activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.propswift.Shared.*
import com.propswift.databinding.LauncherActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LauncherActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: LauncherActivityBinding
    lateinit var handler: Handler
    lateinit var runnable: Runnable

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LauncherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
        handler = Handler()
        runnable = Runnable {
            takeUserToTheNextPage()
        }
        gotonextpage()
    }

    private fun takeUserToTheNextPage() {
        if (!isLoggedIn()) {
            goToActivity(this, WelcomeOneActivity::class.java)
        } else {
            CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
                val e = SessionManager(this@LauncherActivity).fetchu().toString()
                val p = SessionManager(this@LauncherActivity).fetchp().toString()
                Log.d("-------", "initall: $e,  $p")
                viewmodel.refreshtoken(e, p)
                viewmodel.managerCheck()
            }
        }
    }

    fun gotonextpage() {
        handler.postDelayed(runnable, 2000)
    }

}
