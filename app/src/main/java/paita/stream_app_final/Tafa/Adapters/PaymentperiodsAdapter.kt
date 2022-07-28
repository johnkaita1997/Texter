package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentperiodsAdapter(var activity: Activity,
                            var unitprices: UnitPricesObject,
                            private val mContext: Context,
                            var mBottomSheetDialog: BottomSheetDialog,
                            val mpesanumberedittext: EditText,
                            val unitid: String,
                            val formid: String,
                            val subunitslist: Videosperunitname) : RecyclerView.Adapter<PaymentperiodsAdapter.SubjectHolder>() {

    private lateinit var daraja: Daraja
    private lateinit var alertDialog: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_layout, parent, false)
        initiate_Daraje()
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return unitprices.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val subjectobject = unitprices.get(position);

        holder.setperiod(subjectobject.period)
        holder.setamount("KES ${subjectobject.amount}")

        holder.itemView.setOnClickListener {

            val theposition = holder.adapterPosition
            val validatelist = mutableListOf<EditText>(mpesanumberedittext)

            if (activity.validated(validatelist)) {
                val (mpesanumber) = validatelist.map { activity.mytext(it) }
                if (theposition != RecyclerView.NO_POSITION) {
                    val theamount = subjectobject.amount.toDouble().toInt()
                    val userid = activity.getUserId()

                    alertDialog = AlertDialog.Builder(activity).create()
                    showMpesaAlert_Units(formid, alertDialog, activity, formid, unitid)

                    CoroutineScope(Dispatchers.IO).launch() {
                        activity.myViewModel(activity).checkoutUnit(CheckOutUnit(theamount, mpesanumber, unitid, userid))
                    }

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
