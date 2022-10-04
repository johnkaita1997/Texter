package paita.stream_app_final.Tafa.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_free_to_watch.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_topic.spin_kit
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Adapters.FreeToWatchAdapter
import paita.stream_app_final.Tafa.Authentication.LoginActivity

class FreeToWatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_to_watch)
        initall()
    }

    private fun initall() {

        settingsImageviewfreetowatch.setOnClickListener {

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
                            goToActivity_Unfinished(this@FreeToWatchActivity, YourVideos::class.java)
                            true
                        }
                        R.id.logout -> {
                            logoutUser()
                            true
                        }
                        R.id.contact -> {
                            goToActivity_Unfinished(this@FreeToWatchActivity, ContactUsActivity::class.java)
                            true
                        }
                        R.id.myprofile -> {
                            goToActivity_Unfinished(this@FreeToWatchActivity, ProfileActivity::class.java)
                            true
                        }
                        else -> false
                    }
                }
            })
            popup.show()

        }


        spin_kit.visibility = View.VISIBLE

        val subjectid = intent.getStringExtra("subjectid").toString()
        val subjectname = intent.getStringExtra("subjectname").toString()

        watchFullVideos.setOnClickListener {
            finish()
        }

        val viewPool = RecyclerView.RecycledViewPool()
        val layoutManager = LinearLayoutManager(this)
        freetowatchrecyclerview.setLayoutManager(layoutManager)
        freetowatchrecyclerview.setRecycledViewPool(viewPool)
        freetowatchrecyclerview.setItemViewCacheSize(100)

        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

            val freevideos = async { myViewModel(this@FreeToWatchActivity).getFreeVideos(subjectid = subjectid) }

            if (freevideos.await().details!!.isEmpty()) {
                withContext(Dispatchers.Main) {
                    makeLongToast("${subjectname} has no free videos")
                    spin_kit.visibility = View.GONE
                }
                return@launch
            }

            val subject_freetowatch_Adapter = FreeToWatchAdapter(this@FreeToWatchActivity, freevideos.await().details)

            withContext(Dispatchers.Main) {
                freetowatchrecyclerview.setAdapter(subject_freetowatch_Adapter)
                spin_kit.visibility = View.GONE
            }

        }


    }

}