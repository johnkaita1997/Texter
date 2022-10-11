package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_your_videos.*
import kotlinx.android.synthetic.main.activity_your_videos.form_four
import kotlinx.android.synthetic.main.activity_your_videos.form_one
import kotlinx.android.synthetic.main.activity_your_videos.form_three
import kotlinx.android.synthetic.main.activity_your_videos.form_two
import kotlinx.android.synthetic.main.activity_your_videos.settingsImageview
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.ContinueWatchingVideo
import paita.stream_app_final.Tafa.Shared.WatchingDatabase

class YourVideos : AppCompatActivity() {

    private lateinit var formoneid: String;
    private lateinit var formtwoid: String;
    private lateinit var formthreeid: String;
    private lateinit var formfourid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_videos)
        initalll()
    }

    private fun initalll() {

        callTheFormIds()
        initFormButtonClicks()
        continueWatchingInit()
        settingsClick(settingsImageview)
    }

    private fun continueWatchingInit() {


        val database = WatchingDatabase(this@YourVideos).getVideoDao()

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            if (database.getAllContinueWatchingVideo().isEmpty()) {
                withContext(Dispatchers.Main){
//                    makeLongToast("Db Empty")
                }
            } else {

                var videoId = ""
                var videolabel = ""

                contCard.visibility = View.VISIBLE
                database.getAllContinueWatchingVideo().forEach {
                    contlabel.setText(it.videoLabel)
                    videolabel = it.videoLabel
                    videoId = it.videoid
                }

                contCard.setOnClickListener {
                    if (!videoId.equals("") && !videolabel.equals("")) {

                        val database = WatchingDatabase(this@YourVideos).getVideoDao()
                        CoroutineScope(Dispatchers.IO).launch {
                            if (database.getAllContinueWatchingVideo().isNotEmpty()) {
                                database.getAllContinueWatchingVideo().forEach {
                                    database.deleteNote(it)
                                }
                            }
                            database.addContinueWatchingVideo(ContinueWatchingVideo(videoId, videolabel))
                        }
                        playVideos(videoId, videolabel)
                    }
                }


            }

        }

    }

    private fun initFormButtonClicks() {
        form_one.setOnClickListener {
            if (this::formoneid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formoneid)
                intent.putExtra("colorname", "#B330811C")
                intent.putExtra("formname", "Form One")
                intent.putExtra("formnumber", "1")
                intent.putExtra("paid", "paid")
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
                intent.putExtra("paid", "paid")
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
                intent.putExtra("paid", "paid")
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
                intent.putExtra("paid", "paid")
                startActivity(intent)
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


}