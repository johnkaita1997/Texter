package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import kotlinx.android.synthetic.main.my_bottom_sheet_layout.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Tafa.Activities.VideoViewerActivity
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi
import java.lang.Exception

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
        holder.setTopicName(topicName?.lowercase()?.capitalize())
        holder.setIsRecyclable(false)

        var paid = false

        CoroutineScope(Dispatchers.IO).launch() {

            val unitsubscription = async { activity.myViewModel(activity).isUnitSubscribed(unitid) }

            if (isSubjectAlreadySubscribed) {
                holder.makeVisible(activity)
                paid = true
            } else if (istopicSubscribed) {
                holder.makeVisible(activity)
                paid = true
            } else {
                if (unitsubscription.await()) {
                    holder.makeVisible(activity)
                    paid = true
                }
            }

        }

        holder.itemView.setOnClickListener {

            val theposition = holder.adapterPosition
            if (theposition != RecyclerView.NO_POSITION) {

                val selectedItem = subjectobject
                val subunitamount = selectedItem.amount
                val subunitname = selectedItem.name


                CoroutineScope(Dispatchers.IO).launch() {

//                    val formsubscription = async {
//                        activity.myViewModel(activity).isFormSubscribed(formid)
//                    }

//                    val subjectsubscription = async {
//                        activity.myViewModel(activity).isSubjectsubscribed(formid, subjectid)
//                    }

//                    val unitprices = async {
//                        activity.myViewModel(activity).getUnitPrices(unitid.toString())
//                    }

                    if (!paid) {
                        withContext(Dispatchers.Main) {
                            if (subunitamount <= 1) {
                                activity.makeLongToast("Amount selected is invalid or too little")
                            } else showSubscriptionView(unitid, subunitamount, subunitname)
                        }
                    } else {
                        val subunitslist = async {
                            activity.myViewModel(activity).fetchvideosperunitname(unitid)
                        }
                        showUnitVideos(unitid, subunitslist.await())
                    }

//                    withContext(Dispatchers.Main) {
//                        if (!formsubscription.await()) {
//                            if (!subjectsubscription.await()) {
//                            } else showUnitVideos(unitid)
//                        } else showUnitVideos(unitid)
//                    }


                }

            }
        }
    }

    private suspend fun showSubscriptionView(unitid: String, unitamount: Double, subunitname: String) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val sheetView: View = inflater.inflate(R.layout.my_bottom_sheet_layout, null)
        val mpesanumberedittext = sheetView.mpesamobile

        val texView = sheetView.love
        texView.setText("Dear Student, Subscribe to view ${subunitname} videos")

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

                        val invoiceId = activity.myViewModel(activity).checkoutUnit(CheckOutUnit(unitamount.toInt(), mpesanumber, unitid, userid))

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

                                                    val subunitslist = async {
                                                        activity.myViewModel(activity).fetchvideosperunitname(unitid)
                                                    }
                                                    activity.finish()
                                                    showUnitVideos(unitid, subunitslist.await())

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


    private suspend fun showUnitVideos(unitid: String, theobject: Videosperunitname) {


        if (theobject.thedetails.isNotEmpty()) {

            theobject.thedetails.forEachIndexed { index, it ->
                if (index == 0) {
                    val videoid = it?.videoid
                    CoroutineScope(Dispatchers.IO).launch() {
                        val vidocypherResponse = activity.myViewModel(activity).getPlaybackInfo(videoid.toString())

                        withContext(Dispatchers.Main) {
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
                return@forEachIndexed
            }

            theobject.thedetails.forEach {


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
                linearlayoutcontrol.setBackgroundColor(Color.parseColor("#ffffff"))
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