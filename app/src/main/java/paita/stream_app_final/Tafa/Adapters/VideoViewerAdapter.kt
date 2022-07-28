package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.VideoViewerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VideoViewerAdapter(var activity: Activity, var subunitslist: List<Thedetail?>, private val mContext: Context) : RecyclerView.Adapter<VideoViewerAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_subjects, parent, false)

        return ContactHolder(view)
    }

    override fun getItemCount(): Int {
        return subunitslist.size
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {

        val subjectobject = subunitslist.get(position);
        holder.setContactName(subjectobject!!.label)

        holder.itemView.setOnClickListener {

            val theposition = holder.adapterPosition

            if (theposition != RecyclerView.NO_POSITION) {

                val selectedItem = subjectobject
                val id = selectedItem.id
                val videoid = selectedItem.videoid

                CoroutineScope(Dispatchers.IO).launch() {
                    val vidocypherResponse = activity.myViewModel(activity).getPlaybackInfo(videoid)

                    withContext(Dispatchers.Main){
                        if (vidocypherResponse.otp == "") {
                            return@withContext
                        }

                        val otp = vidocypherResponse.otp
                        val playbackinfo = vidocypherResponse.playbackInfo

                        val intent = Intent(mContext, VideoViewerActivity::class.java)
                        intent.putExtra("otp", otp)
                        intent.putExtra("playbackinfo", playbackinfo)
                        mContext.startActivity(intent)
                    }
                }


            }
        }

    }

    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtName: TextView
        private val txtNumber: TextView? = null

        fun setContactName(name: String?) {
            txtName.text = name
        }

        fun setContactNumber(number: String?) {
            txtNumber!!.text = number
        }

        init {
            txtName = itemView.findViewById(R.id.txt_name)
        }

    }
}