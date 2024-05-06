package com.example.sawitprotest.util

import android.text.InputFilter
import android.text.Spanned

class DecimalInputFilter(private val maxValue: Int) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        //dest = sequence of destination characters (current text)
        //dstart = destination start (pointer)
        //dend = destination end (pointer)
        val inputValue = StringBuilder(dest.toString()).apply {
            replace(dstart, dend, source?.subSequence(start, end).toString())
        }.toString()

        val decimalIndex = inputValue.indexOf('.')
        //to check if there is decimal point using -1 as its used to
        val intValue: Int = if (decimalIndex != -1) {
            try {
                inputValue.substring(0, decimalIndex).toInt()
            } catch (e: NumberFormatException) {
                0
            }
        } else {
            try {
                inputValue.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }

        return if (intValue > maxValue) "" else null
    }
}