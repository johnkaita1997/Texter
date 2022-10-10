package paita.stream_app_final.Tafa.Activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FormSubjectAdapter
import paita.stream_app_final.Tafa.Adapters.Subject
import paita.stream_app_final.Tafa.Authentication.LoginActivity
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

        val actualformid = intent.getStringExtra("actualid").toString()
        val colorname = intent.getStringExtra("colorname").toString()
        val actualformname = intent.getStringExtra("formname").toString()
        val formnumber = intent.getStringExtra("formnumber").toString()

        var paid = false
        if (intent.hasExtra("paid")) {
            paid = true
        }

        myviewmodel = ViewModel(this.application, this)

        val layoutManager = LinearLayoutManager(this)
        form_one_subject_rView.setLayoutManager(layoutManager)

        subject_list_adpater = FormSubjectAdapter(this, subjectList, this, colorname, formnumber, actualformid, actualformname, paid)
        fetch_Subject_Lists(actualformid)

        settingsImageviewform.setOnClickListener {

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
                            goToActivity_Unfinished(this@FormActivity, YourVideos::class.java)
                            true
                        }
                        R.id.logout -> {
                            logoutUser()
                            true
                        }
                        R.id.contact -> {
                            goToActivity_Unfinished(this@FormActivity, ContactUsActivity::class.java)
                            true
                        }
                        R.id.myprofile -> {
                            goToActivity_Unfinished(this@FormActivity, ProfileActivity::class.java)
                            true
                        }
                        else -> false
                    }
                }
            })
            popup.show()

        }


    }

    private fun fetch_Subject_Lists(actualid: String) {

        CoroutineScope(Dispatchers.IO).launch() {

            val formid = actualid
            val response = myViewModel(this@FormActivity).fetch_Subject_Lists(formid)

            withContext(Dispatchers.Main) {
                if (response.isEmpty()) {
//                    makeLongToast("Data Not added")
                    spin_kit.visibility = View.GONE
                    return@withContext
                }

                response.forEachIndexed { index, item ->

                    val id = item.id
                    val name = item.name
                    val description = item.description
                    val thumbnail = item.thumbnail
                    val colorcodes = item.color_codes
                    val background = item.background

                    val subject = Subject(description, id, name, thumbnail, colorcodes, background)
                    subjectList.add(subject)

                }

                form_one_subject_rView.setAdapter(subject_list_adpater)
                subject_list_adpater.notifyDataSetChanged();
                spin_kit.visibility = View.GONE

            }

        }


    }
}