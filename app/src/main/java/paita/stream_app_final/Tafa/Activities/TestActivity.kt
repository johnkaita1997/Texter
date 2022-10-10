package paita.stream_app_final.Tafa.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import paita.stream_app_final.Extensions.coroutineexception
import paita.stream_app_final.Extensions.getFormId
import paita.stream_app_final.Extensions.myViewModel
import paita.stream_app_final.R
import kotlinx.coroutines.CoroutineScope as CoroutineScope1

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        CoroutineScope1(Dispatchers.IO).launch() {
            myViewModel(this@TestActivity).getPolygon()
        }

    }
}