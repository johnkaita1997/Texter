package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_topic.*
import kotlinx.android.synthetic.main.topiclist.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.getFormId
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.TopicsAdapter

class TopicsActivity : AppCompatActivity() {

    private lateinit var subjectname: String
    private lateinit var subjectid: String
    private lateinit var subjectdescription: String
    private lateinit var formname: String
    private lateinit var colorname: String
    private lateinit var actualformname: String
    private lateinit var unitsadapter: TopicsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)
        initall()
    }

    private fun initall() {

        val intent = intent

        subjectname = intent.getStringExtra("subjectname").toString()
        subjectid = intent.getStringExtra("subjectid").toString()
        subjectdescription = intent.getStringExtra("subjectdescription").toString()
        formname = intent.getStringExtra("formname").toString()
        colorname = intent.getStringExtra("colorname").toString()
        actualformname = intent.getStringExtra("actualformname").toString()

        setActionBarColor_And_Name(formname, subjectname)

        var amount = 0.0

        CoroutineScope(Dispatchers.IO).launch() {

            val formid = async { getFormId(myViewModel(this@TopicsActivity), formname) }
            val isSubjectSubscribed = async { myViewModel(this@TopicsActivity).isSubjectsubscribed(formid.await(), subjectid) }
            val listOfTopics = async { myViewModel(this@TopicsActivity).fetchTopicList(formid.await(), subjectid) }
            val subjectamount = async { myViewModel(this@TopicsActivity).getsubjectplanlist(formid.await(), subjectid) }

            if (subjectamount.await().isNotEmpty()) {
                subjectamount.await().forEachIndexed { index, subjectPlanListItem ->
                    if (index == 0) {
                        amount = subjectPlanListItem.amount.toDouble()
                        withContext(Dispatchers.Main) {
                            alltopicsAmount.setText("KES ${amount.toString()}")
                            if (amount.toDouble() <= 0) {
                                alltopicsAmount.setText("KES")
                            }
                        }
                    }
                }
            }


            if (!isSubjectSubscribed.await()) {
                withContext(Dispatchers.Main) {
                    subjectSubscriptionCardView.visibility = View.VISIBLE
                }
            }

            withContext(Dispatchers.Main) {
                if (formid.await() == "") {
                    spin_kit.visibility = View.GONE
                    return@withContext
                }
                if (listOfTopics.await().isEmpty()) {
                    spin_kit.visibility = View.GONE
                    return@withContext
                }

                var loaded = false
                val viewPool = RecyclerView.RecycledViewPool()

                val layoutManager = LinearLayoutManager(this@TopicsActivity)
                topiclistRecyclerView.setLayoutManager(layoutManager)
                topiclistRecyclerView.setRecycledViewPool(viewPool)
                topiclistRecyclerView.setItemViewCacheSize(100)
                unitsadapter = TopicsAdapter(this@TopicsActivity, colorname, listOfTopics.await(), this@TopicsActivity, formid.await(), subjectid, colorname, isSubjectSubscribed.await())
                topiclistRecyclerView.setAdapter(unitsadapter)
                spin_kit.visibility = View.GONE

                subjectSubscriptionCardView.setOnClickListener {
                    if (amount <= 1) {
                        makeLongToast("Amount selected is invalid or too little")
                    } else CoroutineScope(Dispatchers.IO).launch() {
                        showCheckOutDialog(formid.await(), subjectid, amount, subjectname)
                    }
                }
            }


        }

        backbuttonvideo.setOnClickListener {
            this.finish()
        }

        rename_EditText()

    }


    private suspend fun showCheckOutDialog(formid: String, subjectid: String, amount: Double, subjectname: String) {

        withContext(Dispatchers.Main) {

            val intent = Intent(this@TopicsActivity, MpesaActivity::class.java)
            intent.putExtra("item", "subject")
            intent.putExtra("subjectname", subjectname)
            intent.putExtra("theamount", amount.toString())
            intent.putExtra("subjectid", subjectid)
            intent.putExtra("formid", formid)
            intent.putExtra("topicname", "")
            intent.putExtra("topicid", "")
            intent.putExtra("unitid", "")
            intent.putExtra("unitname", "")
            startActivity(intent)

        }

    }

    private fun rename_EditText() {
    }


    private fun setActionBarColor_And_Name(formname: String, subjectname: String) {
        videoViewTopText.setText("${this.subjectname.lowercase().capitalize()} ${actualformname} Topics")
        rectangle_88.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorname)));
        spin_kit.setColor(Color.parseColor(colorname))
    }


}