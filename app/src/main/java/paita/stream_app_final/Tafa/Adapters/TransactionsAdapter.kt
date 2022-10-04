package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.TestActivity

class TransactionsAdapter(
    var activity: Activity,
    var transactionobject: List<Detail_Transaction>?,
    var yourvideos_spin_kit: SpinKitView,
) : RecyclerView.Adapter<TransactionsAdapter.SubjectHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.item_transactions, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return transactionobject!!.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
        val topicObject = transactionobject!!.get(position);
        holder.setdata(topicObject, activity, yourvideos_spin_kit)
    }


    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dateCreated: TextView
        private val myrecyclerview: RecyclerView
        private val status: TextView

        fun setdata(transactionobject: Detail_Transaction, activity: Activity, yourvideos_spin_kit: SpinKitView) {

            dateCreated.text =   transactionobject.transaction_date
            status.text = transactionobject.status

            val layoutManager = LinearLayoutManager(activity)
            myrecyclerview.setLayoutManager(layoutManager)
            myrecyclerview.setItemViewCacheSize(100)
            val subtopicAdapter = UnitTransactionsAdapter(activity, transactionobject.units)

            myrecyclerview.setAdapter(subtopicAdapter)

            yourvideos_spin_kit.visibility= View.GONE
        }


        init {
            dateCreated = itemView.findViewById(R.id.transaction_date)
            status = itemView.findViewById(R.id.transaction_status)
            myrecyclerview = itemView.findViewById(R.id.item_transactions_recyclerView)
        }


    }

}