import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.Activities.SmsDetailActivity
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.goToactivityIntent_Unfinished
import com.tafatalkstudent.Shared.makeLongToast
import java.util.Locale

class ContactsAdapter(private val activity: Activity) : PagingDataAdapter<SmsDetail, ContactsAdapter.ViewHolder>(SmsDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.displaycontacts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sms = getItem(position)
        sms?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namedIv: ImageView = itemView.findViewById(R.id.namedIv)
        private val namedTv: TextView = itemView.findViewById(R.id.namedTv)
        private val unnamedRL: RelativeLayout = itemView.findViewById(R.id.unnamedRL)
        private val namedRL: RelativeLayout = itemView.findViewById(R.id.namedRL)
        private val contactName: TextView = itemView.findViewById(R.id.contactName)
        private val messageBody: TextView = itemView.findViewById(R.id.messageBody)
        private val parentCardViewLayout: CardView = itemView.findViewById(R.id.parentCardViewLayout)

        fun bind(sms: SmsDetail) {
            var body = sms.body ?: ""
            if (!sms.isRead!!) {
                body = if (body.length > 100) {
                    "${body.substring(0, 100)}..."
                } else {
                    body
                }
                contactName.setTypeface(null, Typeface.BOLD)
                messageBody.setTypeface(null, Typeface.BOLD)
            } else {
                body = if (body.length > 40) {
                    "${body.substring(0, 40)}..."
                } else {
                    body
                }
                contactName.setTypeface(null, Typeface.NORMAL)
                messageBody.setTypeface(null, Typeface.NORMAL)
            }

            val phoneNumber = sms.phoneNumber.toString()
            val name = sms.name.toString()

            val isNumericOnly = isNumeric(name)
            val colorCode = generateColorCodeFromNumber(phoneNumber)
            val upperCasedName = name.substring(0, 1).uppercase(Locale.ROOT)

            if (!isNumericOnly) {
                namedIv.setBackgroundColor(Color.parseColor(colorCode))
                namedTv.text = upperCasedName
                namedRL.visibility = View.VISIBLE
                unnamedRL.visibility = View.GONE
            } else {
                unnamedRL.visibility = View.VISIBLE
                namedRL.visibility = View.GONE
            }

            contactName.text = name
            messageBody.text = body
            val originalColor = itemView.background


            parentCardViewLayout.setOnClickListener {


                val highlightColor = ContextCompat.getColor(itemView.context, R.color.grey_dull)
                itemView.setBackgroundColor(highlightColor)

                // Restore the original color after a delay (for example, 1 second)
                Handler().postDelayed({
                    itemView.background = originalColor
                }, 10)

                activity.goToactivityIntent_Unfinished(activity, SmsDetailActivity::class.java, mapOf(
                        "phoneNumber" to phoneNumber,
                        "name" to name,
                        "isNumericOnly" to isNumericOnly.toString(),
                        "colorCode" to colorCode,
                        "upperCasedName" to upperCasedName,
                    )
                )

            }
        }
    }

    private fun generateColorCodeFromNumber(input: String): String {
        val numericString = input.replace(Regex("[^0-9]"), "")
        val colorValue = numericString.hashCode()
        return String.format("#%06X", 0xFFFFFF and colorValue)
    }

    private fun isNumeric(input: String): Boolean {
        return input[0].isDigit() || input[0].toString() == "+"
    }
}

class SmsDetailDiffCallback : DiffUtil.ItemCallback<SmsDetail>() {
    override fun areItemsTheSame(oldItem: SmsDetail, newItem: SmsDetail): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }
    override fun areContentsTheSame(oldItem: SmsDetail, newItem: SmsDetail): Boolean {
        return oldItem == newItem
    }
}
