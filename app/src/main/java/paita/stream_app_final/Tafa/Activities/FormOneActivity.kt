package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_form_one.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.getFormId
import paita.stream_app_final.Extensions.goToActivity_Unfinished
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormOneSubjectAdapter
import paita.stream_app_final.Tafa.Adapters.Subject
import paita.stream_app_final.Tafa.Shared.ViewModel


class FormOneActivity : AppCompatActivity() {

    private lateinit var myviewmodel: ViewModel
    private lateinit var subject_list_adpater: FormOneSubjectAdapter
    private val subjectList: ArrayList<Subject> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_one)
        initall()
    }

    private fun initall() {

        /*val progressBar = findViewById<View>(R.id.spin_kit) as ProgressBar
        val doubleBounce: Sprite = DoubleBounce()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.showContextMenu()*/

        myviewmodel = ViewModel(this.application, this)

        val layoutManager = LinearLayoutManager(this)
        form_one_subject_rView.setLayoutManager(layoutManager)

        subject_list_adpater = FormOneSubjectAdapter(subjectList, this)
        fetch_Subject_Lists()

        backbutton.setOnClickListener {
            this.finish()
        }


        yourtopics_formone.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                val formid = getFormId(myViewModel(this@FormOneActivity), "1")
                val intent = Intent(this@FormOneActivity, YourVideos::class.java)
                intent.putExtra("formid", formid)
                intent.putExtra("formname", "Form One")
                intent.putExtra("colorname", "#B330811C")
                startActivity(intent)
            }
        }

    }

    private fun fetch_Subject_Lists() {

        CoroutineScope(Dispatchers.IO).launch() {

            val formid = intent.extras?.getString("formoneid").toString()
            val response = myViewModel(this@FormOneActivity).fetch_Subject_Lists(formid)

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