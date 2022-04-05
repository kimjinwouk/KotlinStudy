package com.kimjinwouk.ibm_calculate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edt_Height : EditText = findViewById(R.id.edt_Height)
        val edt_Weight : EditText = findViewById(R.id.edt_Weight)

        val btn_Result : Button = findViewById(R.id.btn_Result)

        btn_Result.setOnClickListener{

            if(edt_Height.text.isEmpty() ||edt_Weight.text.isEmpty())
            {
                Toast.makeText(this,"빈 값이 있습니다.",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val height : Int = edt_Height.text.toString().toInt()
            val weight : Int = edt_Weight.text.toString().toInt()

            Log.d("MainActivity","height = $height weight = $weight")

            val intent = Intent(this,ResultActivity::class.java)
            intent.putExtra("height",height)
            intent.putExtra("weight",weight)
            startActivity(intent)

        }
    }
}
