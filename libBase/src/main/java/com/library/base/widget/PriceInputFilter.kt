package com.library.base.widget

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class PriceInputFilter: InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence {
        if (source.isEmpty()) {
            return ""
        }
        val pattern = Pattern.compile("^([0-9]|.)*$")
        val matcher = pattern.matcher(source)
        if (!matcher.matches()) {
            return ""
        }
        if (dest.contains(".")) {
            if (source == ".") {
                return ""
            }
            val length = dend - dest.indexOf(".")
            if (length > 2) {
                return ""
            }
        } else {
            if (dest.isEmpty() && source == ".") {
                return ""
            } else if (dest.toString() == "0" && source != ".") {
                return ""
            }
        }
        return source
    }
}