package paita.stream_app_final.Tafa.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.settingsImageview
import kotlinx.android.synthetic.main.activity_paid_videos.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.coroutineexception
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.Extensions.settingsClick
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.PaidVideoAdapter


class PaidVideosView : AppCompatActivity() {

    private var next = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paid_videos)
        initall()
    }

    private fun initall() {

        val formid = intent.getStringExtra("formid").toString()
        val subjectid = intent.getStringExtra("subjectid").toString()

        initPaidVideos(formid, subjectid)
        settingsClick(settingsImageview)

    }


    private fun initPaidVideos(formid: String, subjectid: String) {

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        paidvideosRecycler.setLayoutManager(layoutManager)
        paidvideosRecycler.setItemViewCacheSize(100)

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val paidVideoList = async { myViewModel(this@PaidVideosView).getPaidVideos(formid, subjectid) }
            val videoList = paidVideoList.await().results!! as MutableList

            val padVideoAdapter = PaidVideoAdapter(this@PaidVideosView, videoList)

            withContext(Dispatchers.Main) {
                paidvideosRecycler.setAdapter(padVideoAdapter)
            }

            next = paidVideoList.await().next.toString()

            paidvideosRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged") override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)


                    if (!recyclerView.canScrollVertically(1)) {

                        paidvideos_spin_kit.visibility = View.VISIBLE

                        CoroutineScope(Dispatchers.IO).launch() {
                            val response = myViewModel(this@PaidVideosView).getNext(next, formid, subjectid)
                            next = response.next.toString()

                            if (next.length > 1) {
                                val newVideoList = response.results
                                newVideoList?.forEach {
                                    videoList.add(it)
                                }

                                withContext(Dispatchers.Main) {
                                    padVideoAdapter.notifyDataSetChanged()
                                    paidvideos_spin_kit.visibility = View.GONE

                                }
                            }

                        }

                    }

                }
            })

        }


    }


}