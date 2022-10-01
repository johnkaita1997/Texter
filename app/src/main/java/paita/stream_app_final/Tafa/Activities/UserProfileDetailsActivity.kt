package paita.stream_app_final.Tafa.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import paita.stream_app_final.Extensions.coroutineexception
import paita.stream_app_final.Extensions.getUserId
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R

class UserProfileDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_details)
        initall()
    }

    private fun initall() {


        CoroutineScope(Dispatchers.IO).launch() {

            val userprfoiledetails = async { myViewModel(this@UserProfileDetailsActivity).getUserProfileDetails(getUserId()) }
            val thevideos = userprfoiledetails.await().details

            val usertransactions = async { myViewModel(this@UserProfileDetailsActivity).getTransactions(getUserId()) }
            val transactions = userprfoiledetails.await().details

            val freevideos_await = async { myViewModel(this@UserProfileDetailsActivity).getFreeVideos(subjectid = "") }
            val freeVideos = userprfoiledetails.await().details


        }


    }
}