package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import paita.stream_app_final.R

class UnitTransactionsAdapter(var activity: Activity, var transaction_units: List<String>) : RecyclerView.Adapter<UnitTransactionsAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.item_transaction_units, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return transaction_units.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
        val subjectobject = transaction_units.get(position)
        holder.setTopicName(subjectobject)
    }

    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val unit_transaction_item: TextView
        init {
            unit_transaction_item = itemView.findViewById(R.id.unitTransactionitem)
        }
        fun setTopicName(name: String?) {
            unit_transaction_item.text = name
        }
    }
}