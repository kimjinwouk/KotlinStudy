package com.nfc.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nfc.R
import com.nfc.adapter.nfcRiderAdapter
import com.nfc.data.Rider
import com.nfc.databinding.FragmentWriteBinding
import com.nfc.dialog.RiderDialog
import com.nfc.util.Constants
import com.nfc.util.Constants.Companion.TAG
import com.nfc.viewModel.nfcViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WriteFragment : Fragment(R.layout.fragment_write) {

    //어뎁터
    private lateinit var riderAdapter: nfcRiderAdapter

    //뷰바인딩
    private lateinit var binding: FragmentWriteBinding

    // 뷰모델 생성
    private val viewModel by activityViewModels<nfcViewModel>()

    // Context
    private lateinit var _context: Context

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var riderDialog: RiderDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWriteBinding.bind(view)
        riderAdapter = nfcRiderAdapter(itemClickedListener = {
            /*해당기사의 NFC 태그 입력*/
            showDialog(it)
            viewModel.writeRiderID.value = it.RiderID

        })
        binding.apply {

            riderRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = riderAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            }

            keywordEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                    if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                        if (p0!!.text.length < 2) {
                            Toast.makeText(
                                requireContext(),
                                "최소 2글자 이상의 검색어를 입력해주세요.",
                                Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            searchRider(p0.text.toString())
                        }

                    }
                    return false
                }
            })
            searchButton.setOnClickListener { view -> searchRider(keywordEditText.text.toString()) }
        }

        viewModel.objRiderList.observe(viewLifecycleOwner) {
            it?.let {
                updateAdapterRider(it)
            }
        }

        viewModel.isWriteEvent.observe(viewLifecycleOwner) {
            if (::riderDialog.isInitialized && !riderDialog.isShowing) {
                return@observe
            }

            if (it) {
                viewModel.saveSerialNumber(
                    viewModel.writeRiderID.value!!.toInt(),
                    sharedPref.getString(Constants.SHARED_PREFERENCES_USERID, "")!!.toInt(),
                    viewModel.writeSerialNumber.value.toString()
                )
                viewModel.isWriteEvent.value = false
            }
        }

        viewModel.isWriteResult.observe(viewLifecycleOwner) {
            if (it) {
                if (::riderDialog.isInitialized) {

                    viewModel.objResultMessage.value?.let {
                        if (it.ResultCode.toInt() > 0) {
                            riderDialog.successChangeText()
                        } else {
                            riderDialog.failChangeText()
                        }
                        Toast.makeText(_context, it.ResultMsg, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.isWriteResult.value = false
            }
        }
    }

    private fun showDialog(rider: Rider) {
        riderDialog = RiderDialog(_context, rider)
        riderDialog.show()
    }

    private fun searchRider(Search: String) {
        viewModel.getRiderList(Search, 0)
        if (::riderDialog.isInitialized) {
            Log.d(TAG, "5" + riderDialog.toString())
        }
    }

    private fun updateAdapterRider(riders: List<Rider>) {
        riderAdapter.riders.clear()
        riderAdapter.riders.addAll(riders)
        riderAdapter.notifyDataSetChanged()
    }
}