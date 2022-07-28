package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twigafoods.daraja.Daraja
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubjectPlansAdapter(var activity: Activity, var subjectlist: SubjectPlanList, private val mContext: Context, val formid: String, val subjectid: String, val subjectmpesamobile: EditText) :
        RecyclerView.Adapter<SubjectPlansAdapter.SubjectHolder>() {

    private lateinit var daraja: Daraja
    private lateinit var alertDialog: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_layout, parent, false)
        alertDialog = AlertDialog.Builder(activity).create()
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectlist.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        var validatelist = mutableListOf<EditText>(subjectmpesamobile)

        val subjectobject = subjectlist.get(position)

        holder.setperiod(subjectobject.period)
        holder.setamount(subjectobject.amount)

        holder.itemView.setOnClickListener {
            if (activity.validated(validatelist)) {
                val (mpesamobile) = validatelist.map { activity.mytext(it) }
                val amount = subjectobject.amount.toDouble().toInt()
                val userid = activity.getUserId()
                 showMpesaAlert(formid, alertDialog, activity)
                CoroutineScope(Dispatchers.IO).launch() {
                    activity.myViewModel(activity).checkoutSubject(CheckOutSubject(amount, mpesamobile, subjectid, formid, userid))
                }

            }
        }


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
