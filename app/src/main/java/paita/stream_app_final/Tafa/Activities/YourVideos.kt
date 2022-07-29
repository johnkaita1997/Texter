package paita.stream_app_final.Tafa.Activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_your_videos.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.YourVideosAdapter

class YourVideos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_videos)
        initalll()
    }

    private fun initalll() {

        val formid = intent.getStringExtra("formid").toString()
        val colorname = intent.getStringExtra("colorname").toString()
        val formname = intent.getStringExtra("formname").toString()

        CoroutineScope(Dispatchers.IO).launch() {
            withContext(Dispatchers.Main) {
                yourvideos_spin_kit.setColor(Color.parseColor(colorname))
            }

            val yourvideos = async { myViewModel(this@YourVideos).getYourVideos(formid) }
            val thevideos = yourvideos.await().details

            if (thevideos.isEmpty()) {
                withContext(Dispatchers.Main) {
                    makeLongToast("You haven't subscribed to any videos")
                    yourvideos_spin_kit.visibility = View.GONE
                }
            } else {

                withContext(Dispatchers.Main) {
                    val layoutManager = LinearLayoutManager(this@YourVideos)
                    yourVideosRecyclerView.setLayoutManager(layoutManager)

                    val yourVideosAdapter = YourVideosAdapter(this@YourVideos, thevideos, formname, colorname)
                    yourVideosRecyclerView.setAdapter(yourVideosAdapter)

                    yourvideos_spin_kit.visibility = View.GONE

                }

            }

        }

    }

}