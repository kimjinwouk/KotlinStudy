package a.jinkim.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizer)
    }
    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }
    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    private val recordTimeTextView: CountUpTextView by lazy {
        findViewById(R.id.recordTimeTextView)
    }

    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            recordButton.updateIconWithState(value)
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (State.ON_PLAYING == value)
        }

    private val requiredPermission =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE)

    private var recorder: MediaRecorder? = null

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private var player: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAuidoPermission()
        initViews()
        bindViews()
        initVariables()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            finish()
        }


    }

    private fun bindViews() {
        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> startRecording()
                State.ON_RECORDING -> stopRecording()
                State.AFTER_RECORDING -> startPlayer()
                State.ON_PLAYING -> stopRecording()
            }
        }

        resetButton.setOnClickListener {
            stopPlayer()
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = State.BEFORE_RECORDING
        }
    }

    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }

    private fun requestAuidoPermission() {
        requestPermissions(requiredPermission, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }

        recorder?.start()
        state = State.ON_RECORDING
        soundVisualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        state = State.AFTER_RECORDING
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
    }


    private fun startPlayer() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }

        player?.setOnCompletionListener {
            stopPlayer()
            state = State.AFTER_RECORDING
        }
        player?.start()
        state = State.ON_PLAYING
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
    }

    private fun stopPlayer() {
        player?.release()
        player = null
        state = State.AFTER_RECORDING
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


}