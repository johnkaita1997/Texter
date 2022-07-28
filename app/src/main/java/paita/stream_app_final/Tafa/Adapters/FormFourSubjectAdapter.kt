package paita.stream_app_final.Tafa.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.TopicsActivity

class FormFourSubjectAdapter(var subjectlist: ArrayList<Subject>, private val mContext: Context) : RecyclerView.Adapter<FormFourSubjectAdapter.SubjectHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_subjects_formfour, parent, false)
        return SubjectHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectlist.size
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {

        val subjectobject = subjectlist.get(position);
        holder.setContactName(subjectobject.name.lowercase().capitalize())

        holder.itemView.setOnClickListener {

            val theposition = holder.adapterPosition

            if (theposition != RecyclerView.NO_POSITION) {

                val selectedItem = subjectobject
                val name = selectedItem.name
                val id = selectedItem.id
                val description = selectedItem.description
                val form = "4"

                val intent = Intent(mContext, TopicsActivity::class.java)
                intent.putExtra("subjectname", name)
                intent.putExtra("subjectid", id)
                intent.putExtra("subjectdescription", description)
                intent.putExtra("formname", form)
                intent.putExtra("colorname", "#E36B6B")
                mContext.startActivity(intent)
//                mContext.makeLongToast("You clicked on ${name}")

            }

        }
    }

    class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView
        private val txtNumber: TextView? = null

        fun setContactName(name: String?) {
            txtName.text = name
        }

        fun setContactNumber(number: String?) {
            txtNumber!!.text = number
        }

        init {
            txtName = itemView.findViewById(R.id.txt_name)
        }
    }
}