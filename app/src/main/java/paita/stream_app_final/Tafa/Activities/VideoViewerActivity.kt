package paita.stream_app_final.Tafa.Activities

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.component1
import androidx.core.util.component2
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.PlayerHost
import com.vdocipher.aegis.player.VdoInitParams
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayer.PlaybackEventListener
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import paita.stream_app_final.Extensions.makeLongToast
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Shared.Utils
import paita.stream_app_final.Tafa.Shared.VdoPlayerControlView
import paita.stream_app_final.Tafa.Shared.VdoPlayerControlView.*
import org.json.JSONException
import java.io.IOException


class VideoViewerActivity : AppCompatActivity() {

    private lateinit var theplayer: VdoPlayer
    private lateinit var playerFragment: VdoPlayerSupportFragment
    private lateinit var playerControlView: VdoPlayerControlView
    private lateinit var mSession: MediaSessionCompat
    private var currentOrientation: Int = 0
    private lateinit var otp: String
    private lateinit var playbackinfo: String

    val MEDIA_ACTIONS_PLAY_PAUSE = (PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY_PAUSE)
    val MEDIA_ACTIONS_ALL = MEDIA_ACTIONS_PLAY_PAUSE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_video_viewer)
        initall();
    }

    private fun initall() {

        otp = intent.getStringExtra("otp").toString()
        playbackinfo = intent.getStringExtra("playbackinfo").toString()

        playerControlView = findViewById(R.id.player_control_view);
        playerFragment = supportFragmentManager.findFragmentById(R.id.vdo_player_fragment) as VdoPlayerSupportFragment
        playerFragment.initialize(object : PlayerHost.InitializationListener {

            override fun onInitializationSuccess(playerhost: PlayerHost?, player: VdoPlayer?, wasRestored: Boolean) {

                currentOrientation = getResources().getConfiguration().orientation;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                theplayer = player!!
                player.addPlaybackEventListener(playbackListener)

                playerControlView.setPlayer(player);
                showControls(true);
                initializeMediaSession();
                playerControlView.setFullscreenActionListener(fullscreenToggleListener);
                playerControlView.setControllerVisibilityListener(visibilityListener);
                playerControlView.setVdoParamsGenerator(vdoParamsGenerator);

                val vdoParams = VdoInitParams.createParamsWithOtp(otp, playbackinfo)
                player.load(vdoParams)

            }

            override fun onInitializationFailure(p0: PlayerHost?, p1: ErrorDescription?) {
                TODO("Not yet implemented")
            }

        })

    }


    private val playbackListener: PlaybackEventListener = object : PlaybackEventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        }

        override fun onTracksChanged(tracks: Array<Track?>?, tracks1: Array<Track?>?) {
        }

        override fun onBufferUpdate(bufferTime: Long) {}
        override fun onSeekTo(millis: Long) {
        }

        override fun onProgress(millis: Long) {}
        override fun onPlaybackSpeedChanged(speed: Float) {
        }

        override fun onLoading(vdoInitParams: VdoInitParams) {
        }

        override fun onLoadError(vdoInitParams: VdoInitParams, errorDescription: ErrorDescription) {
            val err = "onLoadError code: " + errorDescription.errorCode + ": " + errorDescription.errorMsg
        }

        override fun onLoaded(vdoInitParams: VdoInitParams) {
//            playerControlView.verifyAndUpdateCaptionsButton()
        }

        override fun onError(vdoParams: VdoInitParams, errorDescription: ErrorDescription) {
            val err = "onError code " + errorDescription.errorCode + ": " + errorDescription.errorMsg
        }

        override fun onMediaEnded(vdoInitParams: VdoInitParams) {
        }
    }


    private val fullscreenToggleListener = FullscreenActionListener { enterFullscreen: Boolean ->
        showFullScreen(enterFullscreen)
        true
    }

    private val visibilityListener = ControllerVisibilityListener { visibility ->
        Log.i("----", "controller visibility $visibility")
        if (currentOrientation === Configuration.ORIENTATION_LANDSCAPE) {
            if (visibility != VISIBLE) {
                showSystemUi(false)
            }
        }
    }

    private val vdoParamsGenerator = label@ VdoParamsGenerator {
        try {
            return@VdoParamsGenerator obtainNewVdoParams()
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread {
                makeLongToast("Error generating new otp and playbackInfo: " + e.javaClass.getSimpleName())
            }
            return@VdoParamsGenerator null
        } catch (e: JSONException) {
            e.printStackTrace()
            runOnUiThread {
                makeLongToast("Error generating new otp and playbackInfo: " + e.javaClass.simpleName)
            }
            return@VdoParamsGenerator null
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        val newOrientation: Int = newConfig.orientation
        val oldOrientation: Int = currentOrientation
        currentOrientation = newOrientation
        Log.i("----",
              "new orientation " + if (newOrientation == Configuration.ORIENTATION_PORTRAIT) "PORTRAIT" else if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) "LANDSCAPE" else "UNKNOWN")
        super.onConfigurationChanged(newConfig)
        if (newOrientation == oldOrientation) {
            Log.i("----", "orientation unchanged")
        } else if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // hide other views
//            findViewById<View>(R.id.title_text).visibility = GONE
//            findViewById<View>(R.id.library_version).visibility = GONE
//            findViewById<View>(R.id.log_container).visibility = GONE
            findViewById<View>(R.id.vdo_player_fragment).layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            playerControlView.fitsSystemWindows = true
            // hide system windows
            showSystemUi(false)
            showControls(false)
        } else {
            // show other views
//            findViewById<View>(R.id.title_text).visibility = VISIBLE
//            findViewById<View>(R.id.library_version).visibility = VISIBLE
//            findViewById<View>(R.id.log_container).visibility = VISIBLE
            findViewById<View>(R.id.vdo_player_fragment).layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            playerControlView.fitsSystemWindows = false
            playerControlView.setPadding(0, 0, 0, 0)
            // show system windows
            showSystemUi(true)
        }
    }

    override fun onBackPressed() {
        if (currentOrientation === Configuration.ORIENTATION_LANDSCAPE) {
            showFullScreen(false)
            playerControlView.setFullscreenState(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun showFullScreen(show: Boolean) {
        Log.v("----", (if (show) "enter" else "exit") + " fullscreen")
        requestedOrientation = if (show) {
            // go to landscape orientation for fullscreen mode
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            // go to portrait orientation
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    private fun showSystemUi(show: Boolean) {
        Log.v("----", (if (show) "show" else "hide") + " system ui")
        if (!show) {
            window.decorView.systemUiVisibility =
                SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    private val uiVisibilityListener: View.OnSystemUiVisibilityChangeListener = View.OnSystemUiVisibilityChangeListener { visibility ->
        Log.v("----", "onSystemUiVisibilityChange")
        // show player controls when system ui is showing
        if (visibility and SYSTEM_UI_FLAG_FULLSCREEN === 0) {
            Log.v("----", "system ui visible, making controls visible")
            showControls(true)
        }
    }

    private class MediaSessionCallback(private val player: VdoPlayer) : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            //resume playing
            player.playWhenReady = true
        }

        override fun onPause() {
            super.onPause()
            //pause playing
            player.playWhenReady = false
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            //Implement this according to your app needs
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            //Implement this according to your app needs
        }
    }

    private val audioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN ->                     //resume playing
                if (theplayer != null) theplayer.setPlayWhenReady(true)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {}
            AudioManager.AUDIOFOCUS_LOSS ->                     //pause playing
                if (theplayer != null) theplayer.setPlayWhenReady(false)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {}
            else -> {}
        }
    }


    private fun showControls(show: Boolean) {
        Log.v("----", (if (show) "show" else "hide") + " controls")
        if (show) {
            playerControlView.show()
        } else {
            playerControlView.hide()
        }
    }


    @WorkerThread @Throws(IOException::class, JSONException::class) private fun obtainNewVdoParams(): VdoInitParams? {
        val (first, second) = Utils.getSampleOtpAndPlaybackInfo()
        val vdoParams = VdoInitParams.Builder().setOtp(first).setPlaybackInfo(second).setPreferredCaptionsLanguage("en").build()
        Log.i("-------", "obtained new otp and playbackInfo")
        return vdoParams
    }


    private fun initializeMediaSession() {
        mSession = MediaSessionCompat(this, "-----")
        mSession.setActive(true)
        MediaControllerCompat.setMediaController(this, mSession.getController())
        val metadata = MediaMetadataCompat.Builder().putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Sample playback").build()
        mSession.setMetadata(metadata)
        val mMediaSessionCallback = MediaSessionCallback(theplayer)
        mSession.setCallback(mMediaSessionCallback)
        val playing = theplayer != null && theplayer.getPlaybackState() !== VdoPlayer.STATE_IDLE && theplayer.getPlaybackState() !== VdoPlayer.STATE_ENDED && theplayer.getPlayWhenReady()
        val state = if (playing) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        if (theplayer != null) {
            updatePlaybackState(state, MEDIA_ACTIONS_ALL, theplayer.getCurrentTime(), theplayer.getPlaybackSpeed())
        }
    }

    private fun updatePlaybackState(@PlaybackStateCompat.State state: Int, position: Long, playBackSpeed: Float) {
        val actions = mSession.controller.playbackState.actions
        updatePlaybackState(state, actions, position, playBackSpeed)
    }

    private fun updatePlaybackState(@PlaybackStateCompat.State state: Int, playbackActions: Long, position: Long, playBackSpeed: Float) {
        val builder = PlaybackStateCompat.Builder().setActions(playbackActions).setState(state, position, playBackSpeed)
        mSession.setPlaybackState(builder.build())
    }


}