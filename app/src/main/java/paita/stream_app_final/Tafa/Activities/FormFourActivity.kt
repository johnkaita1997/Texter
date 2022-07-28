package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormFourSubjectAdapter
import paita.stream_app_final.Tafa.Adapters.Subject
import paita.stream_app_final.Tafa.Shared.ViewModel
import kotlinx.android.synthetic.main.activity_form_four.*
import kotlinx.android.synthetic.main.activity_form_one.*
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.coroutines.*


class FormFourActivity : AppCompatActivity() {

    private lateinit var subject_list_adpater: FormFourSubjectAdapter
    private val subjectList: ArrayList<Subject> = ArrayList()
    private lateinit var myviewmodel: ViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_four)
        initall()
    }

    private fun initall() {

        myviewmodel = ViewModel(this.application, this)

        val layoutManager = LinearLayoutManager(this)
        form_four_subject_rView.setLayoutManager(layoutManager)

        subject_list_adpater = FormFourSubjectAdapter(subjectList, this)

        fetch_Subject_Lists(myviewmodel)

        backbuttonfour.setOnClickListener {
            this.finish()
        }

        yourtopics_formfour.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                val formid = getFormId(myViewModel(this@FormFourActivity), "4")
                val intent = Intent(this@FormFourActivity, YourVideos::class.java)
                intent.putExtra("formid", formid)
                startActivity(intent)
            }
        }

    }

    private fun fetch_Subject_Lists(myviewmodel: ViewModel) {

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val formid = intent.extras?.getString("formfourid").toString()
            val response = myViewModel(this@FormFourActivity).fetch_Subject_Lists(formid)

            withContext(Dispatchers.Main){

                if (response.isEmpty()) {
//                    makeLongToast("Data Not added")
                    spin_kit4.visibility = View.GONE
                    return@withContext
                }

                response.forEachIndexed { index, item ->
                    val id = item.id.toString()
                    val name = item.name.toString()
                    val description = item.description.toString()

                    val subject = Subject(description, id, name)
                    subjectList.add(subject)
                }

                form_four_subject_rView.setAdapter(subject_list_adpater)
                subject_list_adpater.notifyDataSetChanged()
                spin_kit4.visibility = View.GONE
            }

        }


    }

}