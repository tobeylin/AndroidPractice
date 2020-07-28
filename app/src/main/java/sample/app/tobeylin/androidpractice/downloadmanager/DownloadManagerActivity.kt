package sample.app.tobeylin.androidpractice.downloadmanager

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_download_manager.*
import sample.app.tobeylin.androidpractice.R

class DownloadManagerActivity : AppCompatActivity() {

    companion object {
        const val SAMPLE_URL = "https://filesamples.com/samples/audio/mp3/Symphony%20No.6%20(1st%20movement).mp3"
    }

    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)

        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        DownloadBroadcastReceiver().register()

        downloadManagerDownloadButton.setOnClickListener {
            enqueueDownload()
        }
    }

    private fun enqueueDownload() {
        with(downloadManagerDownloadButton) {
            isEnabled = false
            setText(R.string.download_manager_downloading)
        }
        downloadManagerLogDetail.append("Enqueue download request to DownloadManager\n")

        val request = DownloadManager.Request(Uri.parse(SAMPLE_URL)).apply {
        }
        val downloadId = downloadManager.enqueue(request)
    }

    private fun onDownloadComplete() {
        with(downloadManagerDownloadButton) {
            isEnabled = false
            setText(R.string.download_manager_downloaded)
        }
        downloadManagerLogDetail.append("Download completes\n")
    }

    inner class DownloadBroadcastReceiver : BroadcastReceiver() {

        fun register() {
            registerReceiver(DownloadBroadcastReceiver(), IntentFilter().apply {
                addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
                addAction(DownloadManager.ACTION_VIEW_DOWNLOADS)
            })
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> onDownloadComplete()
            }
        }

    }
}
