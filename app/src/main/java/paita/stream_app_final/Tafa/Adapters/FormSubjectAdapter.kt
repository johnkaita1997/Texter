package paita.stream_app_final.Tafa.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormSubjectAdapter.ContactHolder
import paita.stream_app_final.Tafa.Activities.TopicsActivity


class FormSubjectAdapter(var subjectlist: ArrayList<Subject>, private val mContext: Context, val colorname: String, val formnumber: String, val actualformname: String) : RecyclerView.Adapter<ContactHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_subjects, parent, false)
        return ContactHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectlist.size
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {

        val subjectobject = subjectlist.get(position);
        holder.setContactName(subjectobject.name.lowercase().capitalize(), colorname)

        holder.itemView.setOnClickListener {

            val theposition = holder.adapterPosition
            if (theposition != RecyclerView.NO_POSITION) {

                val selectedItem = subjectobject
                
                val name = selectedItem.name
                val id = selectedItem.id
                val description = selectedItem.description

                val intent = Intent(mContext, TopicsActivity::class.java)
                intent.putExtra("subjectname", name)
                intent.putExtra("subjectid", id)
                intent.putExtra("subjectdescription", description)
                intent.putExtra("formname", formnumber)
                intent.putExtra("formnumber", formnumber)
                intent.putExtra("actualformname", actualformname)
                intent.putExtra("colorname", colorname)
                mContext.startActivity(intent)

            }
        }
    }

    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView
        private val txtNumber: TextView? = null

        fun setContactName(name: String?, colorname: String) {
            txtName.text = name
            txtName.setBackgroundColor(Color.parseColor(colorname))
        }

        fun setContactNumber(number: String?) {
            txtNumber!!.text = number
        }

        init {
            txtName = itemView.findViewById(R.id.txt_name)
        }
    }
}