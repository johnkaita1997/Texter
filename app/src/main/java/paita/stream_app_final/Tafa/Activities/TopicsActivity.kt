package paita.stream_app_final.Tafa.Activities

import android.R.id.text2
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_free_to_watch.*
import kotlinx.android.synthetic.main.activity_topic.*
import kotlinx.android.synthetic.main.activity_topic.spin_kit
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.R.*
import paita.stream_app_final.R.color.*
import paita.stream_app_final.Tafa.Adapters.CheckOutSubject
import paita.stream_app_final.Tafa.Adapters.TopicsAdapter
import paita.stream_app_final.Tafa.Authentication.LoginActivity
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi


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
        setContentView(layout.activity_topic)
        initall()
    }

    private fun initall() {


        settingsImageviewtopic.setOnClickListener {

            fun logoutUser() {
                if (sessionManager().logout()) {
                    makeLongToast("You have been logged out successfully")
                    goToActivity(this, LoginActivity::class.java)
                }
            }

            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.pop_menu)

            popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuitem: MenuItem?): Boolean {
                    return when (menuitem!!.getItemId()) {
                        R.id.myvideos -> {
                            goToActivity_Unfinished(this@TopicsActivity, YourVideos::class.java)
                            true
                        }
                        R.id.logout -> {
                            logoutUser()
                            true
                        }
                        R.id.contact -> {
                            goToActivity_Unfinished(this@TopicsActivity, ContactUsActivity::class.java)
                            true
                        }
                        R.id.myprofile -> {
                            goToActivity_Unfinished(this@TopicsActivity, ProfileActivity::class.java)
                            true
                        }
                        else -> false
                    }
                }
            })
            popup.show()

        }


        val intent = intent

        subjectname = intent.getStringExtra("subjectname").toString()
        subjectid = intent.getStringExtra("subjectid").toString()
        subjectdescription = intent.getStringExtra("subjectdescription").toString()
        formname = intent.getStringExtra("formname").toString()
        colorname = intent.getStringExtra("colorname").toString()
        actualformname = intent.getStringExtra("actualformname").toString()

        topicNameTextView.setText(subjectname)

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

    }


    private suspend fun showCheckOutDialog(formid: String, subjectid: String, amount: Double, subjectname: String) {

        withContext(Dispatchers.Main) {

            // Create an alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@TopicsActivity)
            val customLayout: View = getLayoutInflater().inflate(layout.custom_dialog, null)
            builder.setView(customLayout)

            val mydialog: AlertDialog = builder.create()

            val mpesamobile = customLayout.mympesamobile

            CoroutineScope(Dispatchers.IO).launch() {

                val userprfoiledetails = async { myViewModel(this@TopicsActivity).getUserProfileDetails() }
                val userprofile = userprfoiledetails.await().details!!
                val name = userprofile.name
                val text00 = "Dear "
                val text0 = "${name}"
                val textA = ", enter your M-Pesa phone number below to subscribe to "
                val textB = subjectname
                val textC = ". Press "
                val textD = "Pay Now"
                val textE = " button to initiate payment. You will receive a prompt notification on your phone, enter your pin and press OK to complete payment"
                val fulltext = Html.fromHtml(text00+ "<font color=${R.color.formtwocolor}>" + text0 + "</font>" + textA + "<font color=${R.color.formtwocolor}>" + textB + "</font>" + textC +  "<font color=${R.color.formtwocolor}>" + textD + "</font>" + textE)

                withContext(Dispatchers.Main){
                    customLayout.myloveTextDialog.setText(fulltext);
                    customLayout.amountedit.setText("KES : ${amount.toString()}")
                }

            }


            customLayout.cancelnow2.setOnClickListener {
                mydialog.dismiss()
            }
            customLayout.cancelnow.setOnClickListener {
                mydialog.dismiss()
            }

            customLayout.subscribe.setOnClickListener {
                val validatelist = mutableListOf<EditText>(mpesamobile)
                if (validated(validatelist)) {
                    val (mpesanumber) = validatelist.map { mytext(it) }

                    CoroutineScope(Dispatchers.IO).launch() {

                        withContext(Dispatchers.Main) {

                            val theProgressDialog = ProgressDialog(this@TopicsActivity)
                            theProgressDialog.setTitle("Tafa Checkout")
                            theProgressDialog.setMessage("Processing Payment...")

                            makeLongToast("You will receive an M-pesa Prompt Shortly")
                            theProgressDialog.show()

                            val invoiceId = myViewModel(this@TopicsActivity).checkoutSubject(CheckOutSubject(amount.toDouble().toInt(), mpesanumber, subjectid, formid, getUserId()))

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
                                                        if (mydialog.isShowing) {
                                                            mydialog.dismiss()
                                                        }
                                                        makeLongToast("Payment was Successful")
                                                        goToActivity(this@TopicsActivity, MainActivity::class.java)
                                                    } else if (response.body()?.details?.status.equals("PENDING")) {
                                                    }
                                                } else if (response.code() == 400) {
                                                    showAlertDialog("Payment Cancelled")
                                                    state = true
                                                    theProgressDialog.dismiss()
                                                    if (mydialog.isShowing) {
                                                        mydialog.dismiss()
                                                    }
                                                } else {
                                                    showAlertDialog("Payment ${response}")
                                                    state = true
                                                    theProgressDialog.dismiss()
                                                    if (mydialog.isShowing) {
                                                        mydialog.dismiss()
                                                    }
                                                }

                                                if (state == false) {
                                                    runCode()
                                                }
                                            } catch (exception: Exception) {
                                                makeLongToast(exception.toString())
                                                theProgressDialog.dismiss()
                                                if (mydialog.isShowing) {
                                                    mydialog.dismiss()
                                                }
                                            }
                                        }

                                        runCode()

                                    } catch (exception: Exception) {
                                        makeLongToast(exception.toString())
                                        theProgressDialog.dismiss()
                                        if (mydialog.isShowing) {
                                            mydialog.dismiss()
                                        }
                                    }

                                }

                            } else {
                                theProgressDialog.dismiss()
                                if (mydialog.isShowing) {
                                    mydialog.dismiss()
                                }
                            }

                        }
                    }

                }
            }

            mydialog.show()

        }

    }


}