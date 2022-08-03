package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.topiclist.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.MpesaActivity

class TopicsAdapter(
    var activity: Activity,
    var color: String?,
    var subjectlist: Topics,
    private val mContext: Context,
    val formid: String,
    val subjectid: String,
    val colorname: String,
    val isSubjectAlreadySubscribed: Boolean,
) : RecyclerView.Adapter<TopicsAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.topiclist, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectlist.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val topicObject = subjectlist.get(position);
        val topicName = topicObject.name

        holder.setTopicAmount("KES : ${topicObject.amount}")
        if (topicObject.amount.toDouble() <= 0) {
            holder.setTopicAmount("KES")
        }

        holder.setTopicName(topicName?.lowercase()?.capitalize())
        holder.setColor(colorname)

        holder.itemView.thetopicamount.setOnClickListener {
            if (topicObject.amount <= 1) {
                activity.makeLongToast("Amount selected is invalid or too little")
            } else showMpesaDialog(topicObject.amount, subjectid, formid, activity.getUserId(), topicName, activity.getUsername(), topicObject.id)
        }

        CoroutineScope(Dispatchers.Main).launch() {
            val topicSubscription = activity.myViewModel(activity).checkTopicSubscription(topicObject.id)
            if (isSubjectAlreadySubscribed) {
                holder.makeVisible(activity)
            } else {
                if (topicSubscription) {
                    holder.makeVisible(activity)
                }
            }

            holder.setRecyclerViewItems(activity, colorname, topicObject, formid, subjectid, isSubjectAlreadySubscribed, topicSubscription)

        }

    }


    private fun showMpesaDialog(amount: Double, subjectid: String, formid: String, userId: String, topicName: String, username: String, topicId: String) {

        val intent = Intent(activity, MpesaActivity::class.java)
        intent.putExtra("item", "topic")
        intent.putExtra("subjectname", "")
        intent.putExtra("theamount", amount.toString())
        intent.putExtra("subjectid", subjectid)
        intent.putExtra("formid", formid)
        intent.putExtra("topicname", topicName)
        intent.putExtra("topicid", topicId)
        intent.putExtra("unitid", "")
        intent.putExtra("unitname", "")
        activity.startActivity(intent)

    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtName: LinearLayout
        private val myrecyclerview: RecyclerView
        private val topicname: TextView
        private val topicAmount: TextView
        private val linearlayoutcontrol: LinearLayout
        private val playvideo: ImageView

        fun setTopicName(name: String?) {
            topicname.text = name
        }

        fun setTopicAmount(name: String?) {
            topicAmount.text = name
        }

        fun setColor(colorname: String?) {
            txtName.setBackgroundColor(Color.parseColor(colorname))
        }

        suspend fun setRecyclerViewItems(activity: Activity,
                                         colorname: String,
                                         subjectobject: TopicsItem,
                                         formid: String,
                                         subjectid: String,
                                         isSubjectAlreadySubscribed: Boolean,
                                         istopicSubscribed: Boolean) {

            withContext(Dispatchers.Main) {

                val layoutManager = LinearLayoutManager(activity)
                myrecyclerview.setLayoutManager(layoutManager)
                myrecyclerview.setItemViewCacheSize(100)
                val subtopicAdapter = SubTopicsAdapter(activity, colorname, subjectobject.subtopics, activity, formid, subjectid, colorname, isSubjectAlreadySubscribed, istopicSubscribed)
                myrecyclerview.setAdapter(subtopicAdapter)

            }

        }

        suspend fun makeVisible(activity: Activity) {
            withContext(Dispatchers.Main) {
//                playvideo.visibility = View.VISIBLE
//                topicAmount.visibility = View.GONE
                topicAmount.text = "ACTIVE"
//                linearlayoutcontrol.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        init {
            txtName = itemView.findViewById(R.id.txt_name)
            topicname = itemView.findViewById(R.id.thetopicname)
            topicAmount = itemView.findViewById(R.id.thetopicamount)
            myrecyclerview = itemView.findViewById(R.id.subtopicsrecyclerview)
            playvideo = itemView.findViewById(R.id.playvideo)
            linearlayoutcontrol = itemView.findViewById(R.id.linearlayoutcontrol)

        }


    }

}