package paita.stream_app_final.Tafa.Activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormPaymentAdapter
import paita.stream_app_final.Tafa.Adapters.SubjectPaymentAdapter
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.my_bottom_sheet_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionActivity : AppCompatActivity() {

    init {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        initall()
    }

    private fun initall() {


        formone.setOnClickListener {
            entireFormSubcription("1")
        }
        formtwo.setOnClickListener {
            entireFormSubcription("2")
        }
        formthree.setOnClickListener {
            entireFormSubcription("3")
        }
        formfour.setOnClickListener {
            entireFormSubcription("4")
        }


        subjectformone.setOnClickListener {
            subjectFormSubscription("1")
        }
        subjectformtwo.setOnClickListener {
            subjectFormSubscription("2")
        }
        subjectformthree.setOnClickListener {
            subjectFormSubscription("3")
        }
        subjectformfour.setOnClickListener {
            subjectFormSubscription("4")
        }


        browseunits.setOnClickListener {
            goToActivity(this, MainActivity::class.java)
        }


    }

    private fun subjectFormSubscription(theform: String) {

        CoroutineScope(Dispatchers.IO).launch() {
            val formid = getFormId(myViewModel(this@SubscriptionActivity), theform)
            withContext(Dispatchers.Main){
                if (formid == "") {
                    makeLongToast("Class not set up")
                    return@withContext
                }
                showSubjectsDialog(formid)
            }
        }


    }


    private fun entireFormSubcription(theformindexstring: String) {

        CoroutineScope(Dispatchers.IO).launch() {

            val formid = getFormId(myViewModel(this@SubscriptionActivity), theformindexstring)
            val subscribed = myViewModel(this@SubscriptionActivity).isFormSubscribed(formid)

            withContext(Dispatchers.Main){

                if (formid == "") {
                    makeLongToast("Class not set up")
                    return@withContext
                }

                if (!subscribed) {

                    val myfields = myViewModel(this@SubscriptionActivity).getFormAmounts(formid)

                    if (myfields.isEmpty()) {
                        makeLongToast("Fields not entered")
                    }

                    val mBottomSheetDialog = BottomSheetDialog(this@SubscriptionActivity)
                    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val sheetView: View = inflater.inflate(R.layout.subscription_form_bottom_sheet_layout, null)
                    val mpesanumberedittext = sheetView.mpesamobile

                    val layoutManager = LinearLayoutManager(this@SubscriptionActivity)
//                    sheetView.custom_bottom_sheet_recyclerview.setLayoutManager(layoutManager)

                    val formAmountAdapter = FormPaymentAdapter(this@SubscriptionActivity, myfields, applicationContext, mBottomSheetDialog, mpesanumberedittext)
//                    sheetView.custom_bottom_sheet_recyclerview.setAdapter(formAmountAdapter)

                    formAmountAdapter.notifyDataSetChanged();
                    mBottomSheetDialog.setContentView(sheetView)
                    mBottomSheetDialog.show()

                } else {
                    showAlertDialog("You are already subscribed")
                }

            }



        }


    }


    private fun showSubjectsDialog(formid: String) {

        CoroutineScope(Dispatchers.IO).launch() {

            val subjectList = myViewModel(this@SubscriptionActivity).getSubjects(formid)


            withContext(Dispatchers.Main){

                if (subjectList.isEmpty()) {
                    makeLongToast("Subjects not added")
                    return@withContext
                }

                val mBottomSheetDialog = BottomSheetDialog(this@SubscriptionActivity)
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val sheetView: View = inflater.inflate(R.layout.my_bottom_sheet_layout_subjectlist, null)
                val mpesanumberedittext = sheetView.mpesamobile

                val layoutManager = LinearLayoutManager(this@SubscriptionActivity)
//                sheetView.custom_bottom_sheet_recyclerview.setLayoutManager(layoutManager)

                val formAmountAdapter = SubjectPaymentAdapter(this@SubscriptionActivity, subjectList, applicationContext, mBottomSheetDialog, formid)
//                sheetView.custom_bottom_sheet_recyclerview.setAdapter(formAmountAdapter)

                formAmountAdapter.notifyDataSetChanged();
                mBottomSheetDialog.setContentView(sheetView)
                mBottomSheetDialog.show()

            }
        }

    }



}