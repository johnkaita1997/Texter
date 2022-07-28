package paita.stream_app_final.Tafa.Launcher

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_one_launcher.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import paita.stream_app_final.AppConstants.Constants.permission_request
import paita.stream_app_final.Extensions.coroutineexception
import paita.stream_app_final.Extensions.goToActivity
import paita.stream_app_final.Extensions.isLoggedIn
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.MainActivity
import paita.stream_app_final.Tafa.Authentication.LoginActivity
import paita.stream_app_final.Tafa.Authentication.SignUpActivity
import paita.stream_app_final.Tafa.Shared.ConnectionDetector
import paita.stream_app_final.Tafa.Shared.SessionManager

class OneLauncherActivity : AppCompatActivity() {

    var cd = ConnectionDetector(this)
    lateinit var handler: Handler
    lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_launcher)
        getSupportActionBar()?.hide()
        initall()
    }

    private fun initall() {

        handler = Handler()

//        progressBar = progresssec
//        progressBar.indeterminateDrawable = doubleBounce
//        progressBar.setVisibility(View.VISIBLE)

        //load animation from drawable
        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        val bttmanim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        //set animation to image and text
        imgView2.setAnimation(topanim);
        txt.setAnimation(bttmanim);

        //Ask for permissions first
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), permission_request)

        runnable = Runnable {

            if (!isLoggedIn()) {
                goToActivity(this, SignUpActivity::class.java)
            } else {
                CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
                    val e = SessionManager(this@OneLauncherActivity).fetchu().toString()
                    val p = SessionManager(this@OneLauncherActivity).fetchp().toString()
                    Log.d("-------", "initall: $e,  $p")
                    myViewModel(this@OneLauncherActivity).refreshtoken(e, p)
                }
                goToActivity(this, MainActivity::class.java)
            }

        }

    }


    private fun internet_connection_error_Dilog() {
        val alertDialog: android.app.AlertDialog? =
            android.app.AlertDialog.Builder(this).setTitle("Network Error").setMessage("This application requires an active internet connection.").setIcon(R.drawable.tafalogo)
                .setPositiveButton("Fix", DialogInterface.OnClickListener { _, _ -> /*  Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                        startActivityForResult(callGPSSettingIntent, 0);*/
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }).setNegativeButton("Dismiss", DialogInterface.OnClickListener { _, _ ->
                    finish()
                    System.exit(0)
                }).show()
        val btnPositive: Button? = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative: Button? = alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
        val layoutParams = btnPositive?.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative!!.layoutParams = layoutParams
    }

    fun gotonextpage() {
//        handler.postDelayed(runnable, 4000)
        handler.postDelayed(runnable, 2000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (permission_request) {
            100 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Check for connectivity
                if (cd.isConnected) {
                    //Now go to the next page
                    gotonextpage()
                } else {
                    internet_connection_error_Dilog()
                }
            }
            else -> permissionError_Dialog()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun permissionError_Dialog() {
        val alertDialog: android.app.AlertDialog? =
            android.app.AlertDialog.Builder(this).setMessage("This app might malfunction if all the permissions aren't granted.").setCancelable(false).setIcon(R.drawable.tafalogo).setTitle("Warning")
                .setPositiveButton("Dismiss", DialogInterface.OnClickListener { _, _ -> System.exit(0) }).setNegativeButton("", DialogInterface.OnClickListener { _, _ ->
                    finish()
                    System.exit(0)
                }).show()
        val btnPositive = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        val layoutParams = btnPositive?.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
    }

}

