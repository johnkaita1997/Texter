package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.my_bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.topiclist.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi
import java.lang.Exception

class TopicsAdapter(var activity: Activity,
                    var color: String?,
                    var subjectlist: Topics,
                    private val mContext: Context,
                    val formid: String,
                    val subjectid: String,
                    val colorname: String,
                    val isSubjectAlreadySubscribed: Boolean) : RecyclerView.Adapter<TopicsAdapter.SubjectHolder>() {

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
        holder.setTopicName(topicName?.lowercase()?.capitalize())
        holder.setColor(colorname)
        holder.setIsRecyclable(false)

        holder.itemView.thetopicamount.setOnClickListener {
            if (topicObject.amount <= 1) {
                activity.makeLongToast("Amount selected is invalid or too little")
            } else showMpesaDialog(topicObject.amount, subjectid, formid, activity.getUserId(), topicName, activity.getUsername(), topicObject.id)
        }

        CoroutineScope(Dispatchers.IO).launch() {

            val topicSubscription = async { activity.myViewModel(activity).checkTopicSubscription(topicObject.id) }

            if (isSubjectAlreadySubscribed) {
                holder.makeVisible(activity)
            } else {
                if (topicSubscription.await()) {
                    holder.makeVisible(activity)
                }
            }

            holder.setRecyclerViewItems(activity, colorname, topicObject, formid, subjectid, isSubjectAlreadySubscribed, topicSubscription.await())
        }


    }


    private fun showMpesaDialog(amount: Double, subjectid: String, formid: String, userId: String, topicName: String, username: String, topicId: String) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val sheetView: View = inflater.inflate(R.layout.my_bottom_sheet_layout, null)
        val mpesanumberedittext = sheetView.mpesamobile

        val texView = sheetView.love
        texView.setText("Dear Student, Subscribe to view ${topicName} videos")

        val mBottomSheetDialog = BottomSheetDialog(mContext)
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.show()

        sheetView.subscribe.setOnClickListener {

            val validatelist = mutableListOf<EditText>(mpesanumberedittext)
            if (activity.validated(validatelist)) {
                val (mpesanumber) = validatelist.map { activity.mytext(it) }
                val userid = activity.getUserId()

                CoroutineScope(Dispatchers.IO).launch() {

                    withContext(Dispatchers.Main) {

                        val theProgressDialog = ProgressDialog(activity)
                        theProgressDialog.setTitle("Tafa Checkout")
                        theProgressDialog.setMessage("Processing Payment...")

                        activity.makeLongToast("You will receive an M-pesa Prompt Shortly")
                        theProgressDialog.show()
                        val invoiceId = activity.myViewModel(activity).checkoutTopic(CheckOutTopic(amount.toInt(), mpesanumber, topicId, userid))

                        if (!invoiceId.equals("")) {

                            withContext(Dispatchers.Main) {

                                try {

                                    var state = false

                                    suspend fun runCode() {
                                        try {
                                            val response = MyApi().checkInvoiceStatus(invoiceId)

                                            if (response.code() == 200) {
                                                if (response.body()?.details?.status.equals("PAID")) {
                                                    state = true
                                                    theProgressDialog.dismiss()
                                                    activity.makeLongToast("Payment was Successful")
                                                    activity.finish()
                                                } else if (response.body()?.details?.status.equals("PENDING")) {
                                                }
                                            } else if (response.code() == 400) {
                                                activity.showAlertDialog("Payment Cancelled")
                                                state = true
                                                theProgressDialog.dismiss()
                                            } else {
                                                activity.showAlertDialog("Payment ${response}")
                                                state = true
                                                theProgressDialog.dismiss()
                                            }

                                            if (state == false) {
                                                runCode()
                                            }
                                        } catch (exception: Exception) {
                                            activity.makeLongToast(exception.toString())
                                            theProgressDialog.dismiss()
                                        }
                                    }

                                    runCode()

                                } catch (exception: Exception) {
                                    activity.makeLongToast(exception.toString())
                                    theProgressDialog.dismiss()
                                }

                            }

                        } else {
                            theProgressDialog.dismiss()
                        }

                    }
                }


            }
        }

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
                myrecyclerview.setHasFixedSize(true)

                val subtopicAdapter = SubTopicsAdapter(activity, colorname, subjectobject.subtopics, activity, formid, subjectid, colorname, isSubjectAlreadySubscribed, istopicSubscribed)
                myrecyclerview.setAdapter(subtopicAdapter)

            }

        }

        suspend fun makeVisible(activity: Activity) {
            withContext(Dispatchers.Main) {
                playvideo.visibility = View.VISIBLE
                topicAmount.visibility = View.GONE
                linearlayoutcontrol.setBackgroundColor(Color.parseColor("#ffffff"))
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