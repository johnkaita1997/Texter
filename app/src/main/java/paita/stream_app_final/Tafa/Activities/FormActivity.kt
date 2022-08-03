package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormSubjectAdapter
import paita.stream_app_final.Tafa.Adapters.Subject
import paita.stream_app_final.Tafa.Shared.ViewModel


class FormActivity : AppCompatActivity() {

    private lateinit var myviewmodel: ViewModel
    private lateinit var subject_list_adpater: FormSubjectAdapter
    private val subjectList: ArrayList<Subject> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        initall()
    }

    private fun initall() {

        val actualid = intent.getStringExtra("actualid").toString()
        val colorname = intent.getStringExtra("colorname").toString()
        val actualformname = intent.getStringExtra("formname").toString()
        val formnumber = intent.getStringExtra("formnumber").toString()

        spin_kit.setColor(Color.parseColor(colorname))
        rectangle_8.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorname)));

        yourtopics_color.setBackgroundColor(Color.parseColor(colorname))
        welcome_to_.setText("${actualformname} Subjects")
        good_mornin.setText("Welcome To $actualformname")

        myviewmodel = ViewModel(this.application, this)

        val layoutManager = LinearLayoutManager(this)
        form_one_subject_rView.setLayoutManager(layoutManager)

        subject_list_adpater = FormSubjectAdapter(subjectList, this, colorname, formnumber, actualformname)
        fetch_Subject_Lists(actualid)

        backbutton.setOnClickListener {
            this.finish()
        }


        yourtopics_formone.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                val formid = actualid
                val intent = Intent(this@FormActivity, YourVideos::class.java)
                intent.putExtra("formid", formid)
                intent.putExtra("formname", actualformname)
                intent.putExtra("colorname", "#B330811C")
                startActivity(intent)
            }
        }

    }

    private fun fetch_Subject_Lists(actualid: String) {

        CoroutineScope(Dispatchers.IO).launch() {

            val formid = actualid
            val response = myViewModel(this@FormActivity).fetch_Subject_Lists(formid)

            withContext(Dispatchers.Main){
                if (response.isEmpty()) {
//                    makeLongToast("Data Not added")
                    spin_kit.visibility = View.GONE
                    return@withContext
                }

                response.forEachIndexed { index, item ->
                    val id = item.id.toString()
                    val name = item.name.toString()
                    val description = item.description.toString()

                    val subject = Subject(description, id, name)
                    subjectList.add(subject)
                }

                form_one_subject_rView.setAdapter(subject_list_adpater)
                subject_list_adpater.notifyDataSetChanged();
                spin_kit.visibility = View.GONE

            }

        }


    }
}