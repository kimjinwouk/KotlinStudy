package com.qtec.nfc.ui.activity

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nfc.R
import com.nfc.databinding.ActivityMainBinding
import com.nfc.util.Util
import com.nfc.viewModel.nfcViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    // ActivityMainBinding 선언
    private lateinit var binding: ActivityMainBinding

    // NavController 선언
    private lateinit var navController: NavController

    /*
     * NFC 관련정보
     * */
    private var nfcAdapter: NfcAdapter? = null
    private var mlntentFilters = arrayOf<IntentFilter>()
    private var mNFCTechLists = arrayOf<Array<String>>()
    private var pendingIntent: PendingIntent? = null

    // 뷰모델 생성
    private val viewModel by viewModels<nfcViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.apply {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
            navController = navHostFragment.findNavController()
            bottomNavigationView.setupWithNavController(navController)
        }


        init();
    }

    fun init() {
        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) //이 문구는 현재 액티비티에서 해결할시에 사용한다.

        val ndefIntent1 = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        try {
            ndefIntent1.addDataType("*/*")
        } catch (e: Exception) {
            Log.e("TagDispatch", e.toString())
        }


        mlntentFilters = arrayOf<IntentFilter>(ndefIntent1)
        mNFCTechLists = arrayOf(
            arrayOf(
                NfcF::class.java.name,
                NfcA::class.java.name,
                NfcB::class.java.name,
                NfcF::class.java.name,
                NfcV::class.java.name,
                IsoDep::class.java.name,
                MifareClassic::class.java.name,
                MifareUltralight::class.java.name,
                Ndef::class.java.name
            )
        )

        pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_MUTABLE)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.currentDestination?.let {
            when (it.id) {
                R.id.read -> intent?.let { intent ->
                    viewModel.readSerialNumber.value = getSerialNumberfromNfc(intent)
                    viewModel.isReadEvent.value = true
                }
                R.id.write -> intent?.let {
                    viewModel.writeSerialNumber.value = getSerialNumberfromNfc(intent)
                    viewModel.isWriteEvent.value = true
                }
                else -> {}
            }
        }


    }

    fun getSerialNumberfromNfc(intent: Intent): String {
        return Util.byteArrayToHexString((intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) as Tag).id)
            .toString()
    }
//    fun nfcWrite(intent: Intent) {
//        if (viewModel.isWrite.value!!) {
//
//            val detectedTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
//            //writeTag(getTextAsNdef(), detectedTag)
//            viewModel.isWriteSuccess.value = true
//        }
//    }


//    private fun getTextAsNdef(): NdefMessage {
//        val textBytes = viewModel.writeRiderID.value!!.toByteArray()
//        val textRecord = NdefRecord(
//            NdefRecord.TNF_MIME_MEDIA,
//            "text/plain".toByteArray(),
//            byteArrayOf(),
//            textBytes
//        )
//        return NdefMessage(arrayOf(textRecord))
//    }
//
//    fun writeTag(message: NdefMessage, tag: Tag?): Boolean {
//        val size = message.toByteArray().size
//        try {
//            viewModel.writeSerialNumber.value = Util.byteArrayToHexString(tag!!.id)
//            val ndef = Ndef.get(tag)
//            if (ndef != null) {
//                ndef.connect()
//                if (!ndef.isWritable) {
//                    return false
//                }
//                if (ndef.maxSize < size) {
//                    return false
//                }
//                ndef.writeNdefMessage(message)
//            } else {
//                Toast.makeText(
//                    this, "포맷되지 않은 태그이므로 먼저 포맷하고 데이터를 씁니다.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                val formatable = NdefFormatable.get(tag)
//                if (formatable != null) {
//                    try {
//                        formatable.connect()
//                        formatable.format(message)
//                    } catch (ex: IOException) {
//                        ex.printStackTrace()
//                    }
//                }
//                return false
//            }
//        } catch (ex: java.lang.Exception) {
//            ex.printStackTrace()
//            return false
//        }
//        return true
//    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let {
            it.enableForegroundDispatch(
                this,
                pendingIntent,
                null,
                null
            )
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.let {
            it.disableForegroundDispatch(this) //수신
        }
    }
}