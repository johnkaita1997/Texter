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
import paita.stream_app_final.Extensions.playVideos
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
            val videoid = videoObject.videos.get(0).videoid
            activity.playVideos(videoid)
        }


    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}