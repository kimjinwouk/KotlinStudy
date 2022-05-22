package com.kimjinwouk.weather.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kimjinwouk.weather.R

class AddressConfirmDailog()  : DialogFragment() {

    var title: String? = null

    var description: String? = null

    var positiveBtnText: String? = null

    var negativeBtnText: String? = null

    var listener: CustomDialogListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.address_confimg, container, false)
        return view.rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.apply {
            findViewById<TextView>(R.id.text_title)?.text = title
            findViewById<TextView>(R.id.text_description)?.text = description
            findViewById<Button>(R.id.btn_negative)?.text = negativeBtnText
            findViewById<Button>(R.id.btn_negative)?.setOnClickListener {
                dismiss()
                listener?.onClickNegativeBtn()
            }

            findViewById<Button>(R.id.btn_positive)?.text = positiveBtnText
            findViewById<Button>(R.id.btn_positive)?.setOnClickListener {
                dismiss()
                listener?.onClickPositiveBtn()
            }
        }
    }

    class CustomDialogBuilder {

        private val dialog = AddressConfirmDailog()

        fun setTitle(title: String): CustomDialogBuilder {
            dialog.title = title
            return this
        }

        fun setDescription(description: String): CustomDialogBuilder {
            dialog.description = description
            return this
        }

        fun setPositiveBtnText(text: String): CustomDialogBuilder {
            dialog.positiveBtnText = text
            return this
        }

        fun setNegativeBtnText(text: String): CustomDialogBuilder {
            dialog.negativeBtnText = text
            return this
        }

        fun setBtnClickListener(listener: CustomDialogListener): CustomDialogBuilder {
            dialog.listener = listener
            return this
        }



        fun create(): AddressConfirmDailog {
            return dialog
        }
    }


    interface CustomDialogListener {
        fun onClickPositiveBtn()
        fun onClickNegativeBtn()
    }

}