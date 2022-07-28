package paita.stream_app_final.Tafa.Activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.my_bottom_sheet_layout.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.CheckOutSubject
import paita.stream_app_final.Tafa.Adapters.TopicsAdapter
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi
import java.lang.Exception

class TopicsActivity : AppCompatActivity() {

    private lateinit var subjectname: String
    private lateinit var subjectid: String
    private lateinit var subjectdescription: String
    private lateinit var formname: String
    private lateinit var colorname: String
    private lateinit var unitsadapter: TopicsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initall()
    }

    private fun initall() {

        val intent = intent

        subjectname = intent.getStringExtra("subjectname").toString()
        subjectid = intent.getStringExtra("subjectid").toString()
        subjectdescription = intent.getStringExtra("subjectdescription").toString()
        formname = intent.getStringExtra("formname").toString()
        colorname = intent.getStringExtra("colorname").toString()

        setActionBarColor_And_Name()

        val layoutManager = LinearLayoutManager(this)
        topiclistRecyclerView.setLayoutManager(layoutManager)
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

                unitsadapter = TopicsAdapter(this@TopicsActivity, colorname, listOfTopics.await(), this@TopicsActivity, formid.await(), subjectid, colorname, isSubjectSubscribed.await())
                topiclistRecyclerView.setAdapter(unitsadapter)
                unitsadapter.notifyDataSetChanged();
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

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val sheetView: View = inflater.inflate(R.layout.my_bottom_sheet_layout, null)
            val mpesanumberedittext = sheetView.mpesamobile

            val texView = sheetView.love
            texView.setText("Dear Student, Subscribe to view ${subjectname} videos")

            val mBottomSheetDialog = BottomSheetDialog(this@TopicsActivity)
            mBottomSheetDialog.setContentView(sheetView)
            mBottomSheetDialog.show()

            sheetView.subscribe.setOnClickListener {
                val validatelist = mutableListOf<EditText>(mpesanumberedittext)
                if (validated(validatelist)) {
                    val (mpesanumber) = validatelist.map { mytext(it) }

//                    val alertDialog = AlertDialog.Builder(this@TopicsActivity).create()
//                    showMpesaAlert_Units(formid, alertDialog, this@TopicsActivity, formid, subjectid)

                    CoroutineScope(Dispatchers.IO).launch() {

                        withContext(Dispatchers.Main) {

                            val theProgressDialog = ProgressDialog(this@TopicsActivity)
                            theProgressDialog.setTitle("Tafa Checkout")
                            theProgressDialog.setMessage("Processing Payment...")

                            makeLongToast("You will receive an M-pesa Prompt Shortly")
                            theProgressDialog.show()

                            Log.d("-----------------------------------", "showCheckOutDialog: ${getUserId()}")

                            val invoiceId = myViewModel(this@TopicsActivity).checkoutSubject(CheckOutSubject(amount.toInt(), mpesanumber, subjectid, formid, getUserId()))

                            if (!invoiceId.equals("")) {

                                withContext(Dispatchers.Main) {

                                    try {

                                        var state = false

                                        suspend fun runCode() {
                                            try {
                                                val response = MyApi().checkInvoiceStatus(invoiceId)

                                                if (response.code() == 200) {
                                                    if (response.body()?.details?.status.equals("PAID")) {
                                                        state = true
                                                        theProgressDialog.dismiss()
                                                        makeLongToast("Payment was Successful")
                                                        finish()
                                                    } else if (response.body()?.details?.status.equals("PENDING")) {
                                                    }
                                                } else if (response.code() == 400) {
                                                    showAlertDialog("Payment Cancelled")
                                                    state = true
                                                    theProgressDialog.dismiss()
                                                } else {
                                                    showAlertDialog("Payment ${response}")
                                                    state = true
                                                    theProgressDialog.dismiss()
                                                }

                                                if (state == false) {
                                                    runCode()
                                                }
                                            } catch (exception: Exception) {
                                                makeLongToast(exception.toString())
                                                theProgressDialog.dismiss()
                                            }
                                        }

                                        runCode()

                                    } catch (exception: Exception) {
                                        makeLongToast(exception.toString())
                                        theProgressDialog.dismiss()
                                    }

                                }

                            } else {
                                theProgressDialog.dismiss()
                            }

                        }
                    }

                }
            }
        }

    }

    private fun rename_EditText() {
        topic_name.setText("${subjectname.lowercase().capitalize()} Topics")
    }


    private fun setActionBarColor_And_Name() {
        videoViewTopText.setText(subjectname.lowercase().capitalize())
        videoViewTopText.setBackgroundColor(Color.parseColor(colorname))
        rectangle_88.setBackgroundColor(Color.parseColor(colorname))
        spin_kit.setColor(Color.parseColor(colorname))
    }


}