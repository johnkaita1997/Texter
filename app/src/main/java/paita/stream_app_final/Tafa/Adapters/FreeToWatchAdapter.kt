package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_freeto_watch.view.*
import paita.stream_app_final.Extensions.playVideos
import paita.stream_app_final.R


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
            activity.playVideos(videoid, videoObject.label)
        }

    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

}