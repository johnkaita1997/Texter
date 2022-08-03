package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.MpesaActivity
import paita.stream_app_final.Tafa.Activities.VideoViewerActivity

class SubTopicsAdapter(var activity: Activity,
                       var color: String?,
                       var subtopiclist: List<Subtopic>,
                       private val mContext: Context,
                       val formid: String,
                       val subjectid: String,
                       val colorname: String,
                       val isSubjectAlreadySubscribed: Boolean,
                       val istopicSubscribed: Boolean) : RecyclerView.Adapter<SubTopicsAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.subtopiclist, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return subtopiclist.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val subjectobject = subtopiclist.get(position);
        val topicName = subjectobject.name
        val unitid = subjectobject.id

        holder.setTopicAmount("KES : ${subjectobject.amount}")
        if (subjectobject.amount.toDouble() <= 0) {
            holder.setTopicAmount("KES")
        }
        holder.setTopicName(topicName?.lowercase()?.capitalize())

        var paid = false

        CoroutineScope(Dispatchers.Main).launch() {
            val unitsubscription = activity.myViewModel(activity).isUnitSubscribed(unitid)
            if (isSubjectAlreadySubscribed) {
                holder.makeVisible(activity)
                paid = true
            } else if (istopicSubscribed) {
                holder.makeVisible(activity)
                paid = true
            } else {
                if (unitsubscription) {
                    holder.makeVisible(activity)
                    paid = true
                }
            }
        }

        holder.itemView.setOnClickListener {

            val theProgressDialog = ProgressDialog(activity)
            theProgressDialog.setTitle("Fetching")
            theProgressDialog.setMessage("Fetching Video...")
            theProgressDialog.setCancelable(true)
            theProgressDialog.show()

            val theposition = holder.adapterPosition
            if (theposition != RecyclerView.NO_POSITION) {

                val selectedItem = subjectobject
                val subunitamount = selectedItem.amount
                val subunitname = selectedItem.name


                CoroutineScope(Dispatchers.IO).launch() {
                    if (!paid) {
                        withContext(Dispatchers.Main) {
                            if (subunitamount <= 1) {
                                activity.makeLongToast("Amount selected is invalid or too little")
                                theProgressDialog.dismiss()
                            } else {
                                theProgressDialog.dismiss()
                                showSubscriptionView(unitid, subunitamount, subunitname)
                            }
                        }
                    } else {
                        val subunitslist = async {
                            activity.myViewModel(activity).fetchvideosperunitname(unitid)
                        }
                        showUnitVideos(unitid, subunitslist.await(), theProgressDialog)
                    }
                }


            }
        }

    }

    private suspend fun showSubscriptionView(unitid: String, subunitamount: Double, unitname: String) {

        val intent = Intent(activity, MpesaActivity::class.java)
        intent.putExtra("item", "subtopic")
        intent.putExtra("subjectname", "")
        intent.putExtra("theamount", subunitamount.toString())
        intent.putExtra("subjectid", subjectid)
        intent.putExtra("formid", formid)
        intent.putExtra("topicname", "")
        intent.putExtra("topicid", "")
        intent.putExtra("unitid", unitid)
        intent.putExtra("unitname", unitname)
        activity.startActivity(intent)

    }


    private suspend fun showUnitVideos(unitid: String, theobject: Videosperunitname, theProgressDialog: ProgressDialog) {

        CoroutineScope(Dispatchers.Main).launch() {

            if (theobject.thedetails.isNotEmpty()) {

                theobject.thedetails.forEachIndexed { index, it ->
                    if (index == 0) {
                        val videoid = it?.videoid
                        CoroutineScope(Dispatchers.IO).launch() {

                            val vidocypherResponse = activity.myViewModel(activity).getPlaybackInfo(videoid.toString())

                            withContext(Dispatchers.Main) {
                                if (vidocypherResponse.otp == "") {
                                    theProgressDialog.dismiss()
                                    return@withContext
                                }

                                val otp = vidocypherResponse.otp
                                val playbackinfo = vidocypherResponse.playbackInfo

                                val intent = Intent(mContext, VideoViewerActivity::class.java)
                                intent.putExtra("otp", otp)
                                intent.putExtra("playbackinfo", playbackinfo)
                                theProgressDialog.dismiss()
                                mContext.startActivity(intent)
                            }
                        }
                    }
                    return@forEachIndexed
                }

                theobject.thedetails.forEach {


                }
            }
        }


    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val linearlayoutcontrol: LinearLayout
        private val topicname: TextView
        private val topicAmount: TextView
        private val playvideo: ImageView

        fun setTopicName(name: String?) {
            topicname.text = name
        }

        fun setTopicAmount(name: String?) {
            topicAmount.text = name
        }

        suspend fun makeVisible(activity: Activity) {
            withContext(Dispatchers.Main) {
                playvideo.visibility = View.VISIBLE
                topicAmount.visibility = View.GONE
//                linearlayoutcontrol.setBackgroundColor(Color.parseColor("#eb8634"))
            }
        }

        init {
            topicname = itemView.findViewById(R.id.subtopicname)
            topicAmount = itemView.findViewById(R.id.subtopicamount)
            playvideo = itemView.findViewById(R.id.playvideo)
            linearlayoutcontrol = itemView.findViewById(R.id.linearlayoutcontrol)
        }

    }
}