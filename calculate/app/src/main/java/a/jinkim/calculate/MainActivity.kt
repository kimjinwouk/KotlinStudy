package a.jinkim.calculate

import a.jinkim.calculate.model.History
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.room.Room
import androidx.room.RoomDatabase

class MainActivity : AppCompatActivity() {


    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    private val historyLayout : View by lazy{
        findViewById(R.id.historyLayout)
    }

    private val historyLinearLayout : LinearLayout by lazy{
        findViewById<LinearLayout>(R.id.linearlayoutHistory)
    }


    lateinit var db : AppDatabase

    private var isOperator = false
    private var hasOperator = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(
            applicationContext,AppDatabase::class.java,"HistoryDB"
        ).build()

    }

    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonModulo -> operatorButtonClicked("%")
            R.id.buttonMulti -> operatorButtonClicked("*")
        }
    }

    private fun operatorButtonClicked(operator: String) {
        if (expressionTextView.text.isEmpty()) {
            return
        }

        when {
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한 번만 사용 가능합니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }
        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.length() - 1,
            expressionTextView.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true
    }

    private fun numberButtonClicked(number: String) {
        if (isOperator) {
            expressionTextView.append(" ")
        }
        isOperator = false

        val expressionText = expressionTextView.text.split(" ")

        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지 사용 가능합니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        expressionTextView.append(number)
        resultTextView.text = calculateExpression()
    }


    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")
        if (hasOperator.not() && expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1].toString()

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "×" -> (exp1 * exp2).toString()
            "÷" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }


    }

    fun String.isNumber(): Boolean {
        return try {
            this.toBigInteger()
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
        }

        if (!expressionTexts[0].isNumber() || !expressionTexts[2].isNumber()) {
            Toast.makeText(this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
        }

        val expressText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        Thread(Runnable {
            db.historydao().insertHistory(History(null,expressText,resultText))
        }).start()

        resultTextView.text = resultText
        expressionTextView.text = ""

        isOperator = false
        hasOperator = false
    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }


    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historydao().getAll().reversed().forEach{
                    runOnUiThread{
                        val historyraw = LayoutInflater.from(this).inflate(R.layout.history_raw,null,false)
                        historyraw.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                        historyraw.findViewById<TextView>(R.id.resultTextView).text = "= "+it.result
                        historyLinearLayout.addView(historyraw)
                    }
            }
        }).start()


        //TODO 디비 모든 기록 가져오기
        //TODO 뷰에 모든 기록 할당
    }

    fun historyClearButtonClicked(v: View) {
        //TODO 디비에서 모든 기록삭제
        //TODO 뷰에서 모든 기록삭제
        historyLinearLayout.removeAllViews()
        Thread(Runnable {
            db.historydao().deleteAll()
        }).start()
    }

    fun closeHistroyButtonClicked(v: View) {
        historyLayout.isVisible = false

    }


}