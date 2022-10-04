package paita.stream_app_final.Tafa.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_uscontact_us.*
import paita.stream_app_final.Extensions.goToActivity
import paita.stream_app_final.Extensions.goToActivity_Unfinished
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.Extensions.sessionManager
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Authentication.LoginActivity

class ContactUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uscontact_us)
        initall()
    }


    private fun initall() {
        settingsImageviewcontact.setOnClickListener {

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
                            goToActivity_Unfinished(this@ContactUsActivity, YourVideos::class.java)
                            true
                        }
                        R.id.logout -> {
                            logoutUser()
                            true
                        }
                        R.id.contact -> {
                            goToActivity_Unfinished(this@ContactUsActivity, ContactUsActivity::class.java)
                            true
                        }
                        R.id.myprofile -> {
                            goToActivity_Unfinished(this@ContactUsActivity, ProfileActivity::class.java)
                            true
                        }
                        else -> false
                    }
                }
            })
            popup.show()

        }
    }


}