package paita.stream_app_final.Tafa.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_free_to_watch.*
import kotlinx.android.synthetic.main.activity_profile_user_details.*
import kotlinx.android.synthetic.main.activity_profile_user_details.yourvideos_spin_kit
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.TransactionsAdapter
import paita.stream_app_final.Tafa.Authentication.LoginActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user_details)
        initall()
    }

    private fun initall() {

        settingsImageviewprofile.setOnClickListener {

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
                            goToActivity_Unfinished(this@ProfileActivity, YourVideos::class.java)
                            true
                        }
                        R.id.logout -> {
                            logoutUser()
                            true
                        }
                        R.id.contact -> {
                            goToActivity_Unfinished(this@ProfileActivity, ContactUsActivity::class.java)
                            true
                        }
                        R.id.myprofile -> {
                            goToActivity_Unfinished(this@ProfileActivity, ProfileActivity::class.java)
                            true
                        }
                        else -> false
                    }
                }
            })
            popup.show()

        }


        CoroutineScope(Dispatchers.IO).launch() {

            val userprfoiledetails = async { myViewModel(this@ProfileActivity).getUserProfileDetails() }
            val userprofile = userprfoiledetails.await().details!!

            val name = userprofile.name
            val county = userprofile.county
            val school = userprofile.school
            val phone = userprofile.phone
            val date_created = userprofile.date_created

            withContext(Dispatchers.Main) {
                myname.setText(name)
                mycounty.setText(county)
                myschool.setText(school)
                myphone.setText(phone)
                myjoiningdate.setText(date_created)
            }

            val usertransactions = async { myViewModel(this@ProfileActivity).getTransactions() }
            val transactions = usertransactions.await()

            withContext(Dispatchers.Main) {
                val viewPool = RecyclerView.RecycledViewPool()
                val layoutManager = LinearLayoutManager(this@ProfileActivity)
                userprofiledetailsrecyclerview.setLayoutManager(layoutManager)
                userprofiledetailsrecyclerview.setRecycledViewPool(viewPool)
                userprofiledetailsrecyclerview.setItemViewCacheSize(100)
                val transactionAdapter = TransactionsAdapter(this@ProfileActivity, transactions.details, yourvideos_spin_kit)
                userprofiledetailsrecyclerview.setAdapter(transactionAdapter)
            }

            /*val freevideos_await = async { myViewModel(this@UserProfileDetailsActivity).getFreeVideos(subjectid = "") }
            val freeVideos = freevideos_await.await().details*/

        }


    }
}