package paita.stream_app_final.Tafa.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.SubjectFreeAdapter
import paita.stream_app_final.Tafa.Adapters.TrendingVideoAdapter
import paita.stream_app_final.Tafa.Authentication.LoginActivity


class MainActivity : AppCompatActivity() {

    private lateinit var formoneid: String;
    private lateinit var formtwoid: String;
    private lateinit var formthreeid: String;
    private lateinit var formfourid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initall()
    }

    private fun initall() {

        callTheFormIds()

        initTheFreeToWatchVideos()

        initTrendingVideos()

        initFormButtonClicks()

        settingsClick(settingsImageview)

    }

    private fun initFormButtonClicks() {
        form_one.setOnClickListener {
            if (this::formoneid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formoneid)
                intent.putExtra("colorname", "#B330811C")
                intent.putExtra("formname", "Form One")
                intent.putExtra("formnumber", "1")
                startActivity(intent)
            }
        }
        form_two.setOnClickListener {
            if (this::formtwoid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formtwoid)
                intent.putExtra("colorname", "#5968B0")
                intent.putExtra("formname", "Form Two")
                intent.putExtra("formnumber", "2")
                startActivity(intent)
            }
        }
        form_three.setOnClickListener {
            if (this::formthreeid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formthreeid)
                intent.putExtra("colorname", "#B478B5")
                intent.putExtra("formname", "Form Three")
                intent.putExtra("formnumber", "3")
                startActivity(intent)
            }
        }
        form_four.setOnClickListener {
            if (this::formfourid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formfourid)
                intent.putExtra("colorname", "#E36B6B")
                intent.putExtra("formname", "Form Four")
                intent.putExtra("formnumber", "4")
                startActivity(intent)
            }
        }

    }

    private fun initTrendingVideos() {

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trendingvideosRecyclerView.setLayoutManager(layoutManager)
        trendingvideosRecyclerView.setItemViewCacheSize(100)

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val trendingVideoList = async { myViewModel(this@MainActivity).getTrendingVideos() }

            val subject_freetowatch_Adapter = TrendingVideoAdapter(this@MainActivity, trendingVideoList.await())

            withContext(Dispatchers.Main) {
                trendingvideosRecyclerView.setAdapter(subject_freetowatch_Adapter)
            }

        }

    }

    private fun initTheFreeToWatchVideos() {

        val viewPool = RecyclerView.RecycledViewPool()
        val layoutManager = GridLayoutManager(this, 2)

        subjectFreeRecyclerView.setLayoutManager(layoutManager)
        subjectFreeRecyclerView.setRecycledViewPool(viewPool)
        subjectFreeRecyclerView.setItemViewCacheSize(100)

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val subjectList = async { myViewModel(this@MainActivity).getSubjects(formid = "") }

            val subject_freetowatch_Adapter = SubjectFreeAdapter(this@MainActivity, subjectList.await())

            withContext(Dispatchers.Main) {
                subjectFreeRecyclerView.setAdapter(subject_freetowatch_Adapter)
            }

        }

    }

    private fun callTheFormIds() {
        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
            val oneid = async {
                fetchFormId("1")
            }
            val twoid = async {
                fetchFormId("2")
            }
            val threeid = async {
                fetchFormId("3")
            }
            val fourid = async {
                fetchFormId("4")
            }
            formoneid = oneid.await()
            formtwoid = twoid.await()
            formthreeid = threeid.await()
            formfourid = fourid.await()
        }
    }

    private fun permissions() {
        /*
        *     <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission
        android:name="comp.example.pk.SwiftHomeAlaucherpp.permission.MAPS_RECEIVE"
        android:required="false" />
    <uses-permission
        android:name="smartherd.hiltonsteelandcementandroid.permission.STORAGE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.VIBRATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.WRITE_SETTINGS"
        android:required="false"
        tools:ignore="ProtectedPermissions" />
    <uses-permission
        android:name="android.permission.INTERNET"
        android:required="false" />
    <uses-permission
        android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_NETWORK_STATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_FINE_LOCATION"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_WIFI_STATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.WAKE_LOCK"
        android:required="false" />
    <uses-permission
        android:name="android.permission.READ_PHONE_STATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_COARSE_LOCATION"
        android:required="false" />
    <uses-permission
        android:name="android.permission.REQUEST_INSTALL_PACKAGES"
        android:required="false" />
    <uses-permission
        android:name="android.permission.CAMERA"
        android:required="false" />
        * */
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).setTitle("Tafa").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.tafalogo)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finish()
            }).setNegativeButton("Dismis", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }).show()
    }


}