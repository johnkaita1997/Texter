package com.propswift.Dagger

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.propswift.Retrofit.MyApi
import com.propswift.Shared.showAlertDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TestViewModel
@Inject constructor(
    @Named("myapi") private val api: MyApi,
    @ApplicationContext private val appcontext: Context,
) : ViewModel() {
    init {
        val activity: Activity? = (appcontext as MyApplication).currentActivity
        activity!!.showAlertDialog("Love is good")

    }
}
