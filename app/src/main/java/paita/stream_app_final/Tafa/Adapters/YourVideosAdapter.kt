package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.customyourvideos.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.VideoViewerActivity

class YourVideosAdapter(var activity: Activity, var thevideos: ArrayList<YoursDetail>, val formname: String, val colorname: String) : RecyclerView.Adapter<YourVideosAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.customyourvideos, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return thevideos.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val videoObject = thevideos.get(position);
        val topicName = videoObject.name

        holder.itemView.custom_subjectname.setText(videoObject.topic.subject.name)
        holder.itemView.custom_topicname.setText(videoObject.topic.name)
        holder.itemView.custom_subtopicname.setText(videoObject.topic.subtopics.get(0).name)

        holder.itemView.customwatchvideo.setBackgroundColor(Color.parseColor(colorname))
        holder.itemView.customwatchvideo.setBackgroundColor(Color.parseColor(colorname))

        holder.itemView.customwatchvideo.setOnClickListener {

            val theProgressDialog = ProgressDialog(activity)
            theProgressDialog.setTitle("Fetching")
            theProgressDialog.setMessage("Fetching Video...")
            theProgressDialog.setCancelable(true)
            theProgressDialog.show()

            val videoid = videoObject.videos.get(0).videoid

            CoroutineScope(Dispatchers.IO).launch() {
                val vidocypherResponse = activity.myViewModel(activity).getPlaybackInfo(videoid)

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
                    theProgressDialog.dismiss()
                    activity.startActivity(intent)
                }

            }

        }


    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}