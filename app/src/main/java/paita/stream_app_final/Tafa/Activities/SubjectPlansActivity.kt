package paita.stream_app_final.Tafa.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import paita.stream_app_final.Extensions.coroutineexception
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.SubjectPlansAdapter
import kotlinx.android.synthetic.main.activity_subject_plans.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectPlansActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var formid: String
    lateinit var subjectid: String

    init {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_plans)
        initall()
    }

    private fun initall() {

        recyclerView = subjectplansrecyclerview
        formid = intent.extras?.get("formid").toString()
        subjectid = intent.extras?.get("subjectid").toString()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val subjectplanlist = myViewModel(this@SubjectPlansActivity).getsubjectplanlist(formid, subjectid)

            withContext(Dispatchers.Main) {

                if (subjectplanlist.isEmpty()) {
                    return@withContext
                }
                val subjectPlanAdapter = SubjectPlansAdapter(this@SubjectPlansActivity, subjectplanlist, applicationContext, formid, subjectid, subjectmpesamobile)
                recyclerView.setAdapter(subjectPlanAdapter)
                subjectPlanAdapter.notifyDataSetChanged();

            }
        }


    }
}