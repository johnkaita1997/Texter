package paita.stream_app_final.Tafa.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.TopicsActivity
import paita.stream_app_final.Tafa.Adapters.FormSubjectAdapter.ContactHolder
import java.net.URL


class FormSubjectAdapter(var activity: Activity, var subjectlist: ArrayList<Subject>, private val mContext: Context, val colorname: String, val formnumber: String, val actualformname: String) :
        RecyclerView.Adapter<ContactHolder>() {
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
        holder.setContactName(activity, subjectobject.name.lowercase().capitalize(), colorname, subjectobject.thumbnail.toString(), subjectobject)

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
        private var subjectimage: ImageView
        private var subjectbackgroundlinearLayout: LinearLayout
        private var txtNumber: TextView? = null

        fun setContactName(activity: Activity, name: String?, colorname: String, thumbnail: String, subjectobject: Subject) {

            txtName.text = name
//            txtName.setBackgroundColor(Color.parseColor(colorname))
            Picasso.get().load(thumbnail).noFade().into(subjectimage);

            if (subjectobject.name.equals("MATHEMATICS")) {
                subjectbackgroundlinearLayout.setBackgroundResource(R.drawable.mathteacher)
            } else if (subjectobject.name.equals("PHYSICS")) {
                subjectbackgroundlinearLayout.setBackgroundResource(R.drawable.physicsteacher)
            } else if (subjectobject.name.equals("CHEMISTRY")) {
                subjectbackgroundlinearLayout.setBackgroundResource(R.drawable.chemistryteacher)
            } else if (subjectobject.name.equals("BIOLOGY")) {
                subjectbackgroundlinearLayout.setBackgroundResource(R.drawable.biologyteacher)
            }

        }

        init {
            txtName = itemView.findViewById(R.id.txt_name)
            subjectimage = itemView.findViewById(R.id.subjectimage)
            subjectbackgroundlinearLayout = itemView.findViewById(R.id.subjectbackgroundlinearLayout)
        }

    }
}