package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.twigafoods.daraja.Daraja
import com.twigafoods.daraja.DarajaListener
import com.twigafoods.daraja.model.AccessToken
import com.twigafoods.daraja.model.LNMExpress
import com.twigafoods.daraja.model.LNMResult
import com.twigafoods.daraja.util.Env
import paita.stream_app_final.AppConstants.Constants.busienessshortcode
import paita.stream_app_final.AppConstants.Constants.mpesa_callback_url
import paita.stream_app_final.AppConstants.Constants.thepasskey
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.SubjectPlansActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectPaymentAdapter(var activity: Activity, var subjectlist: RetroSubjects, private val mContext: Context, var mBottomSheetDialog: BottomSheetDialog, val formid: String) : RecyclerView.Adapter<SubjectPaymentAdapter.SubjectHolder>() {

    private lateinit var daraja: Daraja

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_layout, parent, false)
//        initiate_Daraje()
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectlist.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val subjectobject = subjectlist.get(position)

        holder.setperiod(subjectobject.name)
        holder.setamount("")

        holder.itemView.setOnClickListener {

            val subjectid = subjectobject.id.toString()


            CoroutineScope(Dispatchers.IO).launch() {
                val subscriptionStatus = activity.myViewModel(activity).isSubjectsubscribed(formid,  subjectid)
                withContext(Dispatchers.Main){
                    if (subscriptionStatus == true) {
                        activity.showAlertDialog("You are already subscribed")
                        return@withContext
                    }

                    val intent = Intent(activity, SubjectPlansActivity::class.java)
                    intent.putExtra("subjectid", subjectid)
                    intent.putExtra("formid", formid)
                    activity.startActivity(intent)
                }
            }


        }

    }

    private fun initiate_Daraje() {

        val consumerkey = "uQH9B9rRvYHvpM2ICYyvBdwR0UE6Pvz4"
        val consumersecret = "DurpnNk6Z21uDjaW"

        daraja = Daraja.with(consumerkey, consumersecret, Env.PRODUCTION, object : DarajaListener<AccessToken> {
            override fun onResult(@NonNull accessToken: AccessToken) {
//                activity.makeLongToast(accessToken.toString())
            }

            override fun onError(error: String) {
                if (activity != null) {
                    activity.makeLongToast(error)
                }
            }
        })
    }
    private fun make_Mpesa_Request(bettamount: String, bettername: String, bettermobile: String) {

        val mobileNumber = bettermobile
        val partyA = mobileNumber
        val partyB = busienessshortcode
        val callback = mpesa_callback_url
        val accountReference = bettername
        val transactionDesc = "Education-Payment"
        val amount = bettamount.toDouble().toInt().toString()
        val passkey = thepasskey

        val lnmExpress = LNMExpress(busienessshortcode, passkey, amount, partyA, partyB, mobileNumber, callback, accountReference, transactionDesc)

        daraja.requestMPESAExpress(lnmExpress, object : DarajaListener<LNMResult> {
            override fun onResult(lnmResult: LNMResult) {
                if (activity.activityisrunning()) {

                    alert = AlertDialog.Builder(activity).setTitle("Tafa").setCancelable(false).setMessage("You will receive an Mpesa prompt shortly").setIcon(R.drawable.tafalogo)
                        .setPositiveButton("", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        }).setNegativeButton("OKAY", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()

                            if (activity.activityisrunning()) {
                                if (mBottomSheetDialog.isShowing) {
                                    mBottomSheetDialog.dismiss()
                                }
                            }

                        }).show()

                }
            }

            override fun onError(error: String) {
                if (activity.activityisrunning()) {
                    activity.showAlertDialog("Mpesa Failed: ${error}")
                }
                if (activity.activityisrunning()) {
                    if (activity.myDialog().isShowing) {
                        activity.myDialog().dismiss()
                    }
                }
            }

        })
    }

    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val period: TextView
        private val amount: TextView

        fun setperiod(name: String?) {
            period.text = name
        }

        fun setamount(number: String?) {
            amount.text = number
        }

        init {
            period = itemView.findViewById(R.id.period)
            amount = itemView.findViewById(R.id.amount)
        }

    }

}
