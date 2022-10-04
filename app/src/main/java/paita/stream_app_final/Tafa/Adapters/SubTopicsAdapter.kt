package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.VideoViewerActivity
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi

class SubTopicsAdapter(var activity: Activity,
                       var color: String,
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
            val textB = unitname
            val textC = ". Press "
            val textD = "Pay Now"
            val textE = " button to initiate payment. You will receive a prompt notification on your phone, enter your pin and press OK to complete payment"
            val fulltext = Html.fromHtml(text00+ "<font color=${R.color.formtwocolor}>" + text0 + "</font>" + textA + "<font color=${R.color.formtwocolor}>" + textB + "</font>" + textC +  "<font color=${R.color.formtwocolor}>" + textD + "</font>" + textE)

            withContext(Dispatchers.Main){
                customLayout.myloveTextDialog.setText(fulltext);
                customLayout.amountedit.setText("KES : ${subunitamount.toString()}")
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

                val (mpesanumber) = validatelist.map {activity.mytext(it) }
                val userid = activity.getUserId()

                CoroutineScope(Dispatchers.IO).launch() {

                    withContext(Dispatchers.Main) {

                        val theProgressDialog = ProgressDialog(activity)
                        theProgressDialog.setTitle("Tafa Checkout")
                        theProgressDialog.setMessage("Processing Payment...")

                        activity.makeLongToast("You will receive an M-pesa Prompt Shortly")
                        theProgressDialog.show()

                        val invoiceId = activity.myViewModel(activity).checkoutUnit(CheckOutUnit(subunitamount.toDouble().toInt(), mpesanumber, unitid, userid))

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
                                                    if (mydialog.isShowing) {
                                                        mydialog.dismiss()
                                                    }
                                                    val subunitslist = async {
                                                        activity.myViewModel(activity).fetchvideosperunitname(unitid)
                                                    }
                                                    activity.finish()
                                                    showUnitVideos(unitid, subunitslist.await(), theProgressDialog)

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
                                                theProgressDialog.dismiss()
                                                if (mydialog.isShowing) {
                                                    mydialog.dismiss()
                                                }
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
                                    theProgressDialog.dismiss()
                                    if (mydialog.isShowing) {
                                        mydialog.dismiss()
                                    }
                                }

                            }

                        } else {
                            theProgressDialog.dismiss()
                            if (mydialog.isShowing) {
                                mydialog.dismiss()
                            }
                        }

                    }
                }
            }
        }

        mydialog.show()

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