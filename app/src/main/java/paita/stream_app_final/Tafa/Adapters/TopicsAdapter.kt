package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.custom_dialog.view.amountedit
import kotlinx.android.synthetic.main.custom_dialog.view.subscribe
import kotlinx.android.synthetic.main.topiclist.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.MainActivity
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi


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

        holder.itemView.thetopicamount.setOnClickListener {
            if (topicObject.amount <= 1) {
                activity.makeLongToast("Amount selected is invalid or too little")
            } else showMpesaDialog(topicObject.amount, subjectid, formid, topicName, activity.getUsername(), topicObject.id)
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


    private fun showMpesaDialog(amount: Double, subjectid: String, formid: String, topicName: String, username: String, topicId: String) {

        // Create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val customLayout: View = activity.getLayoutInflater().inflate(R.layout.custom_dialog, null)
        builder.setView(customLayout)

        val mydialog: AlertDialog = builder.create()

        val mpesamobile = customLayout.mympesamobile

        CoroutineScope(Dispatchers.IO).launch() {

            val userprfoiledetails = async { activity.myViewModel(activity).getUserProfileDetails() }
            val userprofile = userprfoiledetails.await().details!!
            val name = userprofile.name
            val text00 = "Dear "
            val text0 = "${name}"
            val textA = ", enter your M-Pesa phone number below to subscribe to "
            val textB = topicName
            val textC = ". Press "
            val textD = "Pay Now"
            val textE = " button to initiate payment. You will receive a prompt notification on your phone, enter your pin and press OK to complete payment"
            val fulltext = Html.fromHtml(text00+ "<font color=${R.color.formtwocolor}>" + text0 + "</font>" + textA + "<font color=${R.color.formtwocolor}>" + textB + "</font>" + textC +  "<font color=${R.color.formtwocolor}>" + textD + "</font>" + textE)

            withContext(Dispatchers.Main){
                customLayout.myloveTextDialog.setText(fulltext);
                customLayout.amountedit.setText("KES : ${amount.toString()}")
            }

        }

        customLayout.cancelnow2.setOnClickListener {
            mydialog.dismiss()
        }
        customLayout.cancelnow.setOnClickListener {
            mydialog.dismiss()
        }

        customLayout.subscribe.setOnClickListener {

            val validatelist = mutableListOf<EditText>(mpesamobile)

            if (activity.validated(validatelist)) {
                val (mpesanumber) = validatelist.map { activity.mytext(it) }

                val userid = activity.getUserId()
                activity.makeLongToast(userid)

                CoroutineScope(Dispatchers.IO).launch() {

                    withContext(Dispatchers.Main) {

                        val theProgressDialog = ProgressDialog(activity)
                        theProgressDialog.setTitle("Tafa Checkout")
                        theProgressDialog.setMessage("Processing Payment...")

                        activity.makeLongToast("You will receive an M-pesa Prompt Shortly")
                        theProgressDialog.show()
                        val invoiceId = activity.myViewModel(activity).checkoutTopic(CheckOutTopic(amount.toDouble().toInt(), mpesanumber, topicId, userid))

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
                                                    if (mydialog.isShowing) {
                                                        mydialog.dismiss()
                                                    }
                                                    activity.makeLongToast("Payment was Successful")
                                                    activity.goToActivity(activity, MainActivity::class.java)
                                                } else if (response.body()?.details?.status.equals("PENDING")) {
                                                }
                                            } else if (response.code() == 400) {
                                                activity.showAlertDialog("Payment Cancelled")
                                                state = true
                                                theProgressDialog.dismiss()
                                                if (mydialog.isShowing) {
                                                    mydialog.dismiss()
                                                }
                                            } else {
                                                activity.showAlertDialog("Payment ${response}")
                                                state = true
                                                if (mydialog.isShowing) {
                                                    mydialog.dismiss()
                                                }
                                                theProgressDialog.dismiss()
                                            }

                                            if (state == false) {
                                                runCode()
                                            }
                                        } catch (exception: Exception) {
                                            activity.makeLongToast(exception.toString())
                                            theProgressDialog.dismiss()
                                            if (mydialog.isShowing) {
                                                mydialog.dismiss()
                                            }
                                        }
                                    }

                                    runCode()

                                } catch (exception: Exception) {
                                    activity.makeLongToast(exception.toString())
                                    if (mydialog.isShowing) {
                                        mydialog.dismiss()
                                    }
                                    theProgressDialog.dismiss()
                                }

                            }

                        } else {
                            if (mydialog.isShowing) {
                                mydialog.dismiss()
                            }
                            theProgressDialog.dismiss()
                        }

                    }
                }
            }
        }

        mydialog.show()


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