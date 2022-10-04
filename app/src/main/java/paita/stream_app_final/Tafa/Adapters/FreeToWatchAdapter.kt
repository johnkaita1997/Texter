package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_freeto_watch.view.*
import kotlinx.android.synthetic.main.custom_subject_freetowatch.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.FreeToWatchActivity
import paita.stream_app_final.Tafa.Activities.VideoViewerActivity


class FreeToWatchAdapter(var activity: Activity, val videoList: List<Detail_FreeVideos>?) : RecyclerView.Adapter<FreeToWatchAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.custom_freeto_watch, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList!!.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val videoObject = videoList!!.get(position);

        holder.itemView.freetowatchLinearlayout.setOnClickListener {

            val videoid = videoObject.videoid

            val theProgressDialog = ProgressDialog(activity)
            theProgressDialog.setTitle("Fetching")
            theProgressDialog.setMessage("Fetching Video...")
            theProgressDialog.setCancelable(true)
            theProgressDialog.show()

            CoroutineScope(Dispatchers.IO).launch() {

                val vidocypherResponse = activity.myViewModel(activity).getPlaybackInfo(videoid.toString())

                withContext(Dispatchers.Main) {
                    if (vidocypherResponse.otp == "") {
                        theProgressDialog.dismiss()

                        return@withContext
                    }

                    val otp = vidocypherResponse.otp
                    val playbackinfo = vidocypherResponse.playbackInfo

                    val intent = Intent(activity, VideoViewerActivity::class.java)
                    intent.putExtra("otp", otp)
                    intent.putExtra("playbackinfo", playbackinfo)

                    withContext(Dispatchers.Main){
                        theProgressDialog.dismiss()
                    }

                    activity.startActivity(intent)
                }
            }

        }

    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

}