package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_subject_freetowatch.view.*
import kotlinx.android.synthetic.main.custom_trending_videos.view.*
import paita.stream_app_final.Extensions.playVideos
import paita.stream_app_final.R

class TrendingVideoAdapter(var activity: Activity, val trendingVideoList: List<TrendingVideoDetail>?) : RecyclerView.Adapter<TrendingVideoAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.custom_trending_videos, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return trendingVideoList!!.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val subjectObject = trendingVideoList?.get(position);

        holder.setVideoImage(subjectObject, activity)

        holder.itemView.trendingVideoCard.setOnClickListener {
            val videoId = subjectObject?.videoid
            activity.playVideos(videoId.toString(), "Trending")
        }

    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val trendingVideoImage: ImageView

        init {
            trendingVideoImage = itemView.findViewById(R.id.trendingVideoImage)
        }

        fun setVideoImage(subjectObject: TrendingVideoDetail?, activity: Activity) {
            val videoThumbnail = subjectObject?.thumbnail
            Picasso.get().load(videoThumbnail).fit().noFade().into(trendingVideoImage)
        }

    }

}