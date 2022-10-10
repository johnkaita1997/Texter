package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_paid_videos.view.*
import kotlinx.android.synthetic.main.custom_trending_videos.view.*
import paita.stream_app_final.Extensions.playVideos
import paita.stream_app_final.R

class PaidVideoAdapter(var activity: Activity, val paidVideoList: MutableList<PaidVideoResult>) : RecyclerView.Adapter<PaidVideoAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.custom_paid_videos, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return paidVideoList.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val paidVideoObject = paidVideoList.get(position);

        holder.setPaidVideoLabel(paidVideoObject, activity)

        holder.itemView.paidvideolabelname.setOnClickListener {
            val videoId = paidVideoObject.videoid
            activity.playVideos(videoId)
        }

    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val paidvideolabelname: TextView

        init {
            paidvideolabelname = itemView.findViewById(R.id.paidvideolabelname)
        }

        fun setPaidVideoLabel(paidVideoObject: PaidVideoResult, activity: Activity) {
            val label = paidVideoObject.label
            paidvideolabelname.setText(label)
        }


    }

}