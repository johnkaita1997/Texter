package paita.stream_app_final.Tafa.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mpesa.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.CheckOutSubject
import paita.stream_app_final.Tafa.Adapters.CheckOutTopic
import paita.stream_app_final.Tafa.Adapters.CheckOutUnit
import paita.stream_app_final.Tafa.Adapters.Videosperunitname
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi

class MpesaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpesa)
        initall()
    }

    private fun initall() {

        val item = intent.extras?.getString("item").toString()
        val formid = intent.extras?.getString("formid").toString()
        val subjectname = intent.extras?.getString("subjectname").toString()
        val subjectid = intent.extras?.getString("subjectid").toString()
        val theamount = intent.extras?.getString("theamount").toString()
        val topicid = intent.extras?.getString("topicid").toString()
        val topicname = intent.extras?.getString("topicname").toString()
        val unitid = intent.extras?.getString("unitid").toString()
        val unitname = intent.extras?.getString("unitname").toString()

        amountedit.setText("Amount : KES ${theamount}")
        whatsubscriptionPayment.setText("${item.capitalize()} Subscription Payment")

        if (item.equals("subject")) {

            love.setText("Dear Student, Subscribe to view ${subjectname.toUpperCase()} videos")
            subscribe.setOnClickListener {
                val validatelist = mutableListOf<EditText>(mpesamobile)
                if (validated(validatelist)) {
                    val (mpesanumber) = validatelist.map { mytext(it) }

                    CoroutineScope(Dispatchers.IO).launch() {

                        withContext(Dispatchers.Main) {

                            val theProgressDialog = ProgressDialog(this@MpesaActivity)
                            theProgressDialog.setTitle("Tafa Checkout")
                            theProgressDialog.setMessage("Processing Payment...")

                            makeLongToast("You will receive an M-pesa Prompt Shortly")
                            theProgressDialog.show()

                            Log.d("-----------------------------------", "showCheckOutDialog: ${getUserId()}")

                            val invoiceId = myViewModel(this@MpesaActivity).checkoutSubject(CheckOutSubject(theamount.toDouble().toInt(), mpesanumber, subjectid, formid, getUserId()))

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
                                                        goToActivity(this@MpesaActivity, MainActivity::class.java)
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
        if (item.equals("topic")) {
            love.setText("Dear Student, Subscribe to view ${topicname.toUpperCase()} videos")
            subscribe.setOnClickListener {

                val validatelist = mutableListOf<EditText>(mpesamobile)
                if (validated(validatelist)) {
                    val (mpesanumber) = validatelist.map { mytext(it) }
                    val userid = getUserId()

                    CoroutineScope(Dispatchers.IO).launch() {

                        withContext(Dispatchers.Main) {

                            val theProgressDialog = ProgressDialog(this@MpesaActivity)
                            theProgressDialog.setTitle("Tafa Checkout")
                            theProgressDialog.setMessage("Processing Payment...")

                            makeLongToast("You will receive an M-pesa Prompt Shortly")
                            theProgressDialog.show()
                            val invoiceId = myViewModel(this@MpesaActivity).checkoutTopic(CheckOutTopic(theamount.toDouble().toInt(), mpesanumber, topicid, userid))

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
                                                        goToActivity(this@MpesaActivity, MainActivity::class.java)
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
        if (item.equals("subtopic")) {
            love.setText("Dear Student, Subscribe to view ${unitname.toUpperCase()} videos")

            subscribe.setOnClickListener {
                val validatelist = mutableListOf<EditText>(mpesamobile)
                if (validated(validatelist)) {

                    val (mpesanumber) = validatelist.map { mytext(it) }
                    val userid = getUserId()

                    CoroutineScope(Dispatchers.IO).launch() {

                        withContext(Dispatchers.Main) {

                            val theProgressDialog = ProgressDialog(this@MpesaActivity)
                            theProgressDialog.setTitle("Tafa Checkout")
                            theProgressDialog.setMessage("Processing Payment...")

                            makeLongToast("You will receive an M-pesa Prompt Shortly")
                            theProgressDialog.show()

                            val invoiceId = myViewModel(this@MpesaActivity).checkoutUnit(CheckOutUnit(theamount.toDouble().toInt(), mpesanumber, unitid, userid))

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

                                                        val subunitslist = async {
                                                            myViewModel(this@MpesaActivity).fetchvideosperunitname(unitid)
                                                        }
                                                        finish()
                                                        showUnitVideos(unitid, subunitslist.await())

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


    private suspend fun showUnitVideos(unitid: String, theobject: Videosperunitname) {

        if (theobject.thedetails.isNotEmpty()) {

            theobject.thedetails.forEachIndexed { index, it ->
                if (index == 0) {
                    val videoid = it?.videoid
                    CoroutineScope(Dispatchers.IO).launch() {
                        val vidocypherResponse = myViewModel(this@MpesaActivity).getPlaybackInfo(videoid.toString())

                        withContext(Dispatchers.Main) {
                            if (vidocypherResponse.otp == "") {
                                return@withContext
                            }

                            val otp = vidocypherResponse.otp
                            val playbackinfo = vidocypherResponse.playbackInfo

                            val intent = Intent(this@MpesaActivity, VideoViewerActivity::class.java)
                            intent.putExtra("otp", otp)
                            intent.putExtra("playbackinfo", playbackinfo)
                            this@MpesaActivity.startActivity(intent)
                        }
                    }
                }
                return@forEachIndexed
            }

            theobject.thedetails.forEach {


            }
        }

    }


}