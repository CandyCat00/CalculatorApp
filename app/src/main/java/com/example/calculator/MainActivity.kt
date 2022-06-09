package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun equalsAction(view: View) {
        if (view is Button) {
            tvResult.text = calculateResults()
        }
    }

    //turns the result into a string
    private fun calculateResults(): String {
        val fe = finalEquation()
        if (fe.isEmpty()) { return "" }

        val multiDivide = simplifyMD(fe) //do multiplication and division first
        if (multiDivide.isEmpty()) { return "" }

        val result = simplifyAS(multiDivide)

        return result.toString()
    }

    //turns the simplified list into a float
    private fun simplifyAS(list: MutableList<Any>): Float {
        var result = list[0] as Float

        for (i in list.indices) {
            if (list[i] is Char && i != list.lastIndex) {
                val operator = list[i]
                val number = list[i + 1] as Float
                if (operator == '+') {
                    result += number
                }
                if (operator == '-') {
                    result -= number
                }
            }
        }

        return result
    }

    private fun simplifyMD(list: MutableList<Any>): MutableList<Any> {
        var listCopy = list
        while (listCopy.contains('*') || listCopy.contains('/')) {
            listCopy = calcMD(listCopy)
        }
        return listCopy
    }

    private fun calcMD(listCopy: MutableList<Any>): MutableList<Any> {
        val simplified = mutableListOf<Any>()
        var restartIndex = listCopy.size

        for (x in listCopy.indices) {
            if (listCopy[x] is Char && x != listCopy.lastIndex) { //&& x < restartIndex) {
                val operator = listCopy[x]
                val num1 = listCopy[x - 1] as Float
                val num2 = listCopy[x + 1] as Float
                when(operator) {
                    '*' -> {
                        simplified.add(num1 * num2)
                        restartIndex = x + 1
                    }
                    '/' -> {
                        simplified.add(num1 / num2)
                        restartIndex = x + 1
                    }
                    else -> {
                        simplified.add(num1)
                        simplified.add(operator)
                    }
                }
            }
            simplified.add(listCopy[x])
        }
        return listCopy
    }

    private fun finalEquation(): MutableList<Any> {
        val fe = mutableListOf<Any>()
        var number = ""
        for (chara in equation.text) {
            if (chara.isDigit() || chara == '.') { //if this is part of a number
                number += chara
            }
            else { //if it is an operation
                fe.add(number.toFloat()) //add the number created
                number = ""
                fe.add(chara) //add the operation
            }
        }
        if (number != "") { //add in the last number if it exists
            fe.add(number.toFloat())
        }

        return fe
    }

    fun numberAction(view: View) {
        if(view is Button) {
            //clears result when a new number is being typed
            if (!canAddOperation) {
                tvResult.text = ""
            }

            //no more than one decimal per number
            if (view.text == "." && canAddDecimal) {
                equation.append(view.text)
                tvResult.append(view.text)
                canAddDecimal = false
            }
            //for everything that is a number
            else if (view.text != ".") {
                equation.append(view.text)
                tvResult.append(view.text)
            }
            //operation is only true if there is a number to operate on
            canAddOperation = true
        }
    }

    //adds the operator to the string if they are allowed to
    fun operatorAction(view: View) {
        if(view is Button && canAddOperation) {
            equation.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    //clear once to erase result, clear twice to erase equation
    fun clearAll(view: View) {
        if (view is Button) {
            if (tvResult.text.isEmpty()) {
                equation.text = ""
            } else {
                tvResult.text = ""
                canAddDecimal = true
                canAddOperation = false
            }
        }
    }


}