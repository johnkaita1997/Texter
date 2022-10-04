package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_subject_freetowatch.view.*
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.FreeToWatchActivity


class SubjectFreeAdapter(var activity: Activity, val videoList: RetroSubjects) : RecyclerView.Adapter<SubjectFreeAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.custom_subject_freetowatch, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val subjectObject = videoList.get(position);

        holder.setTopicName(subjectObject, activity)

        holder.itemView.suject_freetowatch_linearlayout.setOnClickListener {

            val subjectid = subjectObject.id
            val subjectname = subjectObject.name

            val intent = Intent(activity, FreeToWatchActivity::class.java)
            intent.putExtra("subjectid", subjectid)
            intent.putExtra("subjectname", subjectname)
            activity.startActivity(intent)

        }

    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val topicname: TextView
        private val freetowatchImage: ImageView
        private val suject_freetowatch_linearlayout: LinearLayout

        init {
            topicname = itemView.findViewById(R.id.freeToWatch_Subject)
            freetowatchImage = itemView.findViewById(R.id.freetowatchImage)
            suject_freetowatch_linearlayout = itemView.findViewById(R.id.suject_freetowatch_linearlayout)
        }

        fun setTopicName(subjectObject: RetroSubjectsItem, activity: Activity) {

            topicname.text = subjectObject.name
            suject_freetowatch_linearlayout.setBackgroundColor(Color.parseColor(subjectObject.color_codes))
            Picasso.get().load(subjectObject.thumbnail).noFade().into(freetowatchImage);
        }

    }

}