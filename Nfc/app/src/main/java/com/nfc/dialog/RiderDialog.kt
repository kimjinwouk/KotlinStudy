package com.nfc.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.nfc.data.Rider
import com.nfc.databinding.DialogRiderBinding
import com.nfc.util.Util

class RiderDialog (
    context: Context,
    val rider : Rider
)  : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: DialogRiderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
        binding = DialogRiderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()


    }

    private fun initViews() = with(binding) {
        // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog가 사라지지 않도록
        setCancelable(false)

        // background를 투명하게 만듦
        // (중요) Dialog는 내부적으로 뒤에 흰 사각형 배경이 존재하므로, 배경을 투명하게 만들지 않으면
        // corner radius의 적용이 보이지 않는다.
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        infoTextView.text = "${rider.RName}(${Util.formatTelNumber(rider.RPda)})"
        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!
        cancelButton.setOnClickListener {
            dismiss()
        }
        var frameAnimation = nfcImageView?.getBackground() as AnimationDrawable
        nfcImageView?.post(object : Runnable {
            override fun run() {
                frameAnimation.start()
            }
        })
    }

    fun successChangeText(){
        binding.titleTextView.text = "태그에 해당 기사님의 정보를 정상적으로 입력하였습니다."
        binding.cancelButton.text = "성공"
    }
    fun failChangeText(){
        binding.titleTextView.text = "태그 입력에 실패하였습니다."
        binding.cancelButton.text = "실패"
    }
}