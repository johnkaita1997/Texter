package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_form_four.*
import kotlinx.android.synthetic.main.activity_form_one.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormThreeSubjectAdapter
import paita.stream_app_final.Tafa.Adapters.Subject
import paita.stream_app_final.Tafa.Shared.ViewModel
import kotlinx.android.synthetic.main.activity_form_three.*
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FormThreeActivity : AppCompatActivity() {

    private lateinit var myviewmodel: ViewModel
    private lateinit var subject_list_adpater: FormThreeSubjectAdapter
    private val subjectList: ArrayList<Subject> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_three)
        initall()
    }

    private fun initall() {

        myviewmodel = ViewModel(this.application, this)

        val layoutManager = LinearLayoutManager(this)
        form_three_subject_rView.setLayoutManager(layoutManager)

        subject_list_adpater = FormThreeSubjectAdapter(subjectList, this)
        fetch_Subject_Lists()

        backbuttonthree.setOnClickListener {
            this.finish()
        }

        yourtopics_formthree.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                val formid = getFormId(myViewModel(this@FormThreeActivity), "3")
                val intent = Intent(this@FormThreeActivity, YourVideos::class.java)
                intent.putExtra("formid", formid)
                startActivity(intent)
            }
        }

    }

    private fun fetch_Subject_Lists() {

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val formid = intent.extras?.getString("formthreeid").toString()
            val response = myViewModel(this@FormThreeActivity).fetch_Subject_Lists(formid)

            withContext(Dispatchers.Main){
                if (response.isEmpty()) {
//                    makeLongToast("Data Not added")
                    spin_kit3.visibility = View.GONE
                    return@withContext
                }

                response.forEachIndexed { index, item ->
                    val id = item.id.toString()
                    val name = item.name.toString()
                    val description = item.description.toString()

                    val subject = Subject(description, id, name)
                    subjectList.add(subject)
                }

                form_three_subject_rView.setAdapter(subject_list_adpater)
                subject_list_adpater.notifyDataSetChanged();
                spin_kit3.visibility = View.GONE

            }
        }



    }

}