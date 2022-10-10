package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_your_videos.*
import kotlinx.android.synthetic.main.activity_your_videos.form_four
import kotlinx.android.synthetic.main.activity_your_videos.form_one
import kotlinx.android.synthetic.main.activity_your_videos.form_three
import kotlinx.android.synthetic.main.activity_your_videos.form_two
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.coroutineexception
import paita.stream_app_final.Extensions.fetchFormId
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.YourVideosAdapter

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